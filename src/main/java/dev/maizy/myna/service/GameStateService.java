package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.gibberish.Gibberish;
import dev.maizy.myna.db.entity.GameEntity;
import dev.maizy.myna.db.entity.GamePlayerEntity;
import dev.maizy.myna.db.entity.GamePlayerId;
import dev.maizy.myna.db.repository.GamePlayerRepository;
import dev.maizy.myna.db.repository.GameRepository;
import dev.maizy.myna.db.repository.RulesetRepository;
import dev.maizy.myna.game_state.GameState;
import dev.maizy.myna.utils.AccessKeyGenerator;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GameStateService {

  public record GameAndPlayer(GameEntity game, GamePlayerEntity player){}

  private final Gibberish gibberish;
  private final AccessKeyGenerator accessKeyGenerator;
  private final GameRepository gameRepository;
  private final GamePlayerRepository gamePlayerRepository;
  private final RulesetRepository rulesetRepository;

  public GameStateService(
      GameRepository gameRepository,
      GamePlayerRepository gamePlayerRepository,
      RulesetRepository rulesetRepository) {
    this.gameRepository = gameRepository;
    this.gamePlayerRepository = gamePlayerRepository;
    this.rulesetRepository = rulesetRepository;
    this.gibberish = new Gibberish();
    this.accessKeyGenerator = new AccessKeyGenerator();
  }

  @Transactional
  public GameEntity createGame(
      String rulesetId, String owner, Map<String, String> playerNames, Optional<String> ownerPlayerId) {

    final var now = ZonedDateTime.now();
    final var maybeRulesetSummary = rulesetRepository.findSummaryById(rulesetId);
    if (maybeRulesetSummary.isEmpty()) {
      throw new GameStateServiceErrors.GameStateChangeError(
          GameState.created, GameState.upcomming, "Unknown ruleset with id = '" + rulesetId + "'"
      );
    }

    final var rulesetSummary = maybeRulesetSummary.get();
    final var gameId = gibberish.generateSlug();

    final var rulesetPlayers = rulesetSummary.getRuleset().players();
    final var players = IntStream
        .range(0, rulesetPlayers.size())
        .mapToObj(playerIndex -> {
          final var rulesetPlayer = rulesetPlayers.get(playerIndex);
          final var gamePlayerId = new GamePlayerId();
          gamePlayerId.setGameId(gameId);
          gamePlayerId.setRulesetPlayerId(rulesetPlayer.id());

          final var playerEntity = new GamePlayerEntity();
          playerEntity.setId(gamePlayerId);
          playerEntity.setName(playerNames.getOrDefault(rulesetPlayer.id(), ""));
          playerEntity.setRulesetOrder(playerIndex);
          playerEntity.setJoinKey(accessKeyGenerator.generateAccessKey());
          ownerPlayerId.ifPresent(id -> {
            if (rulesetPlayer.id().equals(id)) {
              playerEntity.setUid(owner);
              playerEntity.setJoinedAt(now);
            }
          });
          return playerEntity;
        })
        .toList();

    final var gameEntity = new GameEntity();
    gameEntity.setId(gameId);
    gameEntity.setOwnerUid(owner);
    gameEntity.setRulesetId(rulesetSummary.getId());
    gameEntity.setState(GameState.upcomming);
    gameEntity.setCreatedAt(now);

    try {
      gameRepository.save(gameEntity);
    } catch (RuntimeException dbError) {
      throw new GameStateServiceErrors.GameStateChangeError(
          GameState.created, GameState.upcomming, "Unable to persist game",
          dbError
      );
    }

    try {
      gamePlayerRepository.saveAll(players);
    } catch (RuntimeException dbError) {
      throw new GameStateServiceErrors.GameStateChangeError(
          GameState.created, GameState.upcomming, "Unable to persist game players state",
          dbError
      );
    }
    return gameEntity;
  }

  public Optional<GameAndPlayer> checkJoinKey(String gameId, String joinKey) {
    final var maybeGame = gameRepository.findByIdAndFetchRuleset(gameId);
    if (maybeGame.isEmpty()) {
      throw new GameStateServiceErrors.GameNotFound("Game not found", gameId);
    } else {
      final var game = maybeGame.get();
      checkGameState(
          game,
          Arrays.asList(GameState.created, GameState.upcomming, GameState.launched),
          "It's too late to join the game"
      );
      return gamePlayerRepository.findFirstByIdGameIdAndJoinKey(game.getId(), joinKey)
          .map(player -> new GameAndPlayer(game, player));
    }
  }

  @Transactional
  public void joinGame(GameAndPlayer gameAndPlayer, String uid) {
    final var playerEntity = gameAndPlayer.player();
    playerEntity.setUid(uid);
    playerEntity.setJoinedAt(ZonedDateTime.now());
    gamePlayerRepository.save(playerEntity);
  }

  private void checkGameState(GameEntity game, List<GameState> allowedStates, String errorMessage) {
    if (!allowedStates.contains(game.getState())) {
      throw new GameStateServiceErrors.ActionIsForbiddenInCurrentGameState(
          errorMessage, game.getId(), game.getState(), allowedStates
      );
    }
  }
}
