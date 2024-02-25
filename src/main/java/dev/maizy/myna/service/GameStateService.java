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
import dev.maizy.myna.ruleset.Player;
import dev.maizy.myna.service.game_messages.GameMessageBus;
import dev.maizy.myna.service.game_messages.GameMessageHandler;
import dev.maizy.myna.service.game_messages.GameStateContext;
import dev.maizy.myna.utils.AccessKeyGenerator;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class GameStateService {

  public record GameAccessAuth(GameEntity game, @Nullable GamePlayerEntity player){

    public GameAccessAuth {
      Objects.requireNonNull(game);
    }

    public Optional<Player> getRulesetPlayer() {
      if (player() != null) {
        return game.getRuleset().getRuleset().getPlayerById(player().getId().getRulesetPlayerId());
      } else {
        return Optional.empty();
      }
    }

    public GameAccessAuth withPlayer(GamePlayerEntity updatedPlayer) {
      return new GameAccessAuth(game, updatedPlayer);
    }

    public GameAccessAuth withGame(GameEntity updatedGame) {
      return new GameAccessAuth(updatedGame, player);
    }
  }

  private final Gibberish gibberish;
  private final AccessKeyGenerator accessKeyGenerator;
  private final GameRepository gameRepository;
  private final GamePlayerRepository gamePlayerRepository;
  private final RulesetRepository rulesetRepository;
  private final GameMessageHandler gameMessageHandler;
  private final ObjectProvider<GameMessageBus> gameMessageBusFactory;

  public GameStateService(
      GameRepository gameRepository,
      GamePlayerRepository gamePlayerRepository,
      RulesetRepository rulesetRepository,
      GameMessageHandler gameMessageHandler,
      ObjectProvider<GameMessageBus> gameMessageBusFactory) {
    this.gameRepository = gameRepository;
    this.gamePlayerRepository = gamePlayerRepository;
    this.rulesetRepository = rulesetRepository;
    this.gameMessageHandler = gameMessageHandler;
    this.gameMessageBusFactory = gameMessageBusFactory;

    this.gibberish = new Gibberish();
    this.accessKeyGenerator = new AccessKeyGenerator();
  }

  @Transactional
  public GameAccessAuth createGame(
      String rulesetId, String owner, Map<String, String> playerNames, Optional<String> ownerPlayerId) {

    final var now = ZonedDateTime.now();
    final var rulesetSummary = rulesetRepository.findSummaryById(rulesetId)
        .orElseThrow(() -> new GameStateServiceErrors.GameStateServiceException(
            "Unknown ruleset with id = '" + rulesetId + "'"
        ));

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
      throw new GameStateServiceErrors.GameStateServiceException("Unable to persist game", dbError);
    }

    final Iterable<GamePlayerEntity> savedPlayers;
    try {
      savedPlayers = gamePlayerRepository.saveAll(players);
    } catch (RuntimeException dbError) {
      throw new GameStateServiceErrors.GameStateServiceException("Unable to persist game players state", dbError);
    }

    final var maybeOwnerPlayer = ownerPlayerId.flatMap(id ->
        StreamSupport.stream(savedPlayers.spliterator(), false)
            .filter(player -> player.getId().getRulesetPlayerId().equals(id))
            .findFirst()
    );

    onGameStateChange(gameEntity, null);

    return new GameAccessAuth(gameEntity, maybeOwnerPlayer.orElse(null));
  }

  public GameAccessAuth checkGameAccessAuthByUid(String gameId, String uid) {
    final var maybeGame = gameRepository.findByIdAndFetchRuleset(gameId);
    if (maybeGame.isEmpty()) {
      throw new GameStateServiceErrors.GameNotFound(gameId);
    }

    final var game = maybeGame.get();
    final var maybeGameAccessAuth = gamePlayerRepository
        .findFirstByIdGameIdAndUidOrderByRulesetOrder(game.getId(), uid)
        .map(gamePlayerEntity ->
            new GameAccessAuth(game, gamePlayerEntity)
        )
        .or(() -> {
          if (onlyForOwner(game, uid)) {
            return Optional.of(new GameAccessAuth(game, null));
          } else {
            return Optional.empty();
          }
        });

    return maybeGameAccessAuth.orElseThrow(() -> new GameStateServiceErrors.Forbidden(game));
  }

  public GameAccessAuth checkGameAccessAuthByUidAndPlayerId(String gameId, String uid, String rulesetPlayerId) {
    final var maybeGame = gameRepository.findByIdAndFetchRuleset(gameId);
    if (maybeGame.isEmpty()) {
      throw new GameStateServiceErrors.GameNotFound(gameId);
    }
    final var game = maybeGame.get();
    final var maybeGameAccessAuth = gamePlayerRepository
        .findFirstByIdGameIdAndIdRulesetPlayerIdAndUid(game.getId(), rulesetPlayerId, uid)
        .map(gamePlayerEntity ->
            new GameAccessAuth(game, gamePlayerEntity)
        );
    return maybeGameAccessAuth.orElseThrow(() -> new GameStateServiceErrors.Forbidden(game));
  }

  public GameAccessAuth checkJoinGame(String gameId, String joinKey) {
    final var maybeGame = gameRepository.findByIdAndFetchRuleset(gameId);
    if (maybeGame.isEmpty()) {
      throw new GameStateServiceErrors.GameNotFound(gameId);
    } else {
      final var game = maybeGame.get();
      final var gameAccess = gamePlayerRepository.findFirstByIdGameIdAndJoinKey(game.getId(), joinKey)
          .map(player -> new GameAccessAuth(game, player))
          .orElseThrow(() -> new GameStateServiceErrors.NotValidJoinKey(game, joinKey));
      checkGameState(
          game,
          Arrays.asList(GameState.created, GameState.upcomming, GameState.launched),
          "It's too late to join the game"
      );
      return gameAccess;
    }
  }

  @Transactional
  public GameAccessAuth joinGame(GameAccessAuth gameAccessAuth, String uid) {
    checkGameState(
        gameAccessAuth.game(),
        Arrays.asList(GameState.created, GameState.upcomming, GameState.launched),
        "It's too late to join the game"
    );
    final var playerEntity = gameAccessAuth.player();
    playerEntity.setUid(uid);
    playerEntity.setJoinedAt(ZonedDateTime.now());

    final GamePlayerEntity updatePlayerEntity;
    try {
      updatePlayerEntity = gamePlayerRepository.save(playerEntity);
    } catch (RuntimeException dbError) {
      throw new GameStateServiceErrors.GameStateServiceException(
          "Unable to join the game",
          dbError
      );
    }
    return gameAccessAuth.withPlayer(updatePlayerEntity);
  }

  public void checkLobbyAccess(GameAccessAuth gameAccessAuth) {
    checkGameState(
        gameAccessAuth.game(),
        Arrays.asList(GameState.created, GameState.upcomming),
        "The game is already running"
    );
  }

  public boolean isAllowedToLaunchGame(GameAccessAuth gameAccessAuth, String uid) {
    return onlyForOwner(gameAccessAuth, uid);
  }

  public List<GamePlayerEntity> getPlayers(GameEntity game) {
    return gamePlayerRepository.findAllByIdGameIdOrderByRulesetOrderAscNameAsc(game.getId());
  }

  @Transactional
  public GameAccessAuth launchGame(GameAccessAuth gameAccessAuth, String uid) {
    final var game = gameAccessAuth.game();
    if (!isAllowedToLaunchGame(gameAccessAuth, uid)) {
      throw new GameStateServiceErrors.Forbidden("You aren't allowed to launch the game", game);
    }
    checkGameState(
        game,
        Arrays.asList(GameState.created, GameState.upcomming),
        "The game has already started"
    );

    final var previousState = game.getState();
    game.setState(GameState.launched);
    final GameEntity updatedGame;
    try {
      updatedGame = gameRepository.save(game);
    } catch (RuntimeException dbError) {
      throw new GameStateServiceErrors.GameStateServiceException(
          "Unable to launch the game",
          dbError
      );
    }
    onGameStateChange(updatedGame, previousState);
    return gameAccessAuth.withGame(updatedGame);
  }

  public void checkPlaygroundAccess(GameAccessAuth gameAccessAuth) {
    checkGameState(
        gameAccessAuth.game(),
        List.of(GameState.launched),
        gameAccessAuth.game().getState() == GameState.finished
            ? "The game has finished" : "The game hasn't started yet"
    );
  }

  public boolean isAllowedToControlGame(GameAccessAuth gameAccessAuth, String uid) {
    return onlyForOwner(gameAccessAuth, uid);
  }

  @Transactional
  public GameAccessAuth endGame(GameAccessAuth gameAccessAuth, String uid) {
    final var game = gameAccessAuth.game();
    if (!isAllowedToControlGame(gameAccessAuth, uid)) {
      throw new GameStateServiceErrors.Forbidden("You aren't allowed to end the game", game);
    }
    checkGameState(
        game,
        Arrays.asList(GameState.created, GameState.upcomming, GameState.launched),
        "The game has already ended"
    );

    final var previousState = game.getState();
    game.setState(GameState.finished);
    game.setFinishedAt(ZonedDateTime.now());

    final GameEntity updatedGame;
    try {
      updatedGame = gameRepository.save(game);
    } catch (RuntimeException dbError) {
      throw new GameStateServiceErrors.GameStateServiceException(
          "Unable to end the game",
          dbError
      );
    }

    onGameStateChange(updatedGame, previousState);
    return gameAccessAuth.withGame(updatedGame);
  }

  public void checkCreditsAccess(GameAccessAuth gameAccessAuth) {
    checkGameState(
        gameAccessAuth.game(),
        List.of(GameState.finished),
        "The game hasn't started yet"
    );
  }

  private boolean onlyForOwner(GameAccessAuth gameAccessAuth, String uid) {
    return onlyForOwner(gameAccessAuth.game(), uid);
  }

  private boolean onlyForOwner(GameEntity game, String uid) {
    return game.isOwner(uid);
  }

  private void checkGameState(GameEntity game, List<GameState> allowedStates, @Nullable String message) {
    if (!allowedStates.contains(game.getState())) {
      if (message != null) {
        throw new GameStateServiceErrors.ForbiddenInCurrentGameState(message, game, allowedStates);
      } else {
        throw new GameStateServiceErrors.ForbiddenInCurrentGameState(game, allowedStates);
      }
    }
  }

  private void checkGameStateChange(GameEntity game, List<GameState> allowedCurrentStates, GameState toState) {
    if (!allowedCurrentStates.contains(game.getState())) {
      throw new GameStateServiceErrors.GameStateChangeForbidden(game, allowedCurrentStates, toState);
    }
  }

  private void onGameStateChange(GameEntity game, GameState previousState) {
    final var context = new GameStateContext(game);
    final var bus = gameMessageBusFactory.getObject(null, game.getId());
    gameMessageHandler.onGameStateChange(context, previousState, game.getState(), bus);
  }

}
