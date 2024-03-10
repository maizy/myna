package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.game_message.BroadcastMessage;
import dev.maizy.myna.game_message.EventType;
import dev.maizy.myna.game_message.PlayerMessage;
import dev.maizy.myna.game_message.Request;
import dev.maizy.myna.game_message.event.ImmutableGameState;
import dev.maizy.myna.game_message.event.ImmutablePlayerState;
import dev.maizy.myna.game_message.event.ImmutablePlayersState;
import dev.maizy.myna.game_message.response.ImmutableFullView;
import dev.maizy.myna.game_state.GameState;
import dev.maizy.myna.service.GameItemsService;
import dev.maizy.myna.service.GameStateService;
import dev.maizy.myna.service.GameStateServiceErrors;
import dev.maizy.myna.service.PlayerStateService;
import dev.maizy.myna.ws.PlayerWebsocketContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameMessageHandler {

  private static final Logger log = LoggerFactory.getLogger(GameMessageHandler.class);

  private final PlayerStateService playerStateService;
  private final GameItemsService gameItemsService;
  private final GameStateService gameStateService;

  public GameMessageHandler(
      PlayerStateService playerStateService,
      GameItemsService gameItemsService,
      GameStateService gameStateService) {
    this.playerStateService = playerStateService;
    this.gameItemsService = gameItemsService;
    this.gameStateService = gameStateService;

    gameStateService.subscribeToGameStateChange(this);
  }

  public void onPlayerMessageReceived(PlayerMessage event, GameMessageBus bus) {
    if (event.message() instanceof Request request) {
      onRequestReceived(request, event.context(), bus);
    }
  }

  protected void onRequestReceived(Request request, PlayerWebsocketContext wsContext, GameMessageBus bus) {
    switch (request.requestType()) {
      case get_full_view ->
        gameItemsService.loadGameModel(request.gameId()).ifPresent(model -> {
          // TODO: process model based on current player, not required for now
          final var response = ImmutableFullView.builder()
              .oid(request.oid())
              .gameId(wsContext.gameId())
              .gameModel(model)
              .build();
          bus.responseWithMessage(response);
        });

      case get_game_state -> {
        final GameState state;
        try {
          state = gameStateService.getGameState(wsContext.gameId());
        } catch (GameStateServiceErrors.GameStateServiceException e) {
          log.error("unable to get game state", e);
          return;
        }
        final var response = dev.maizy.myna.game_message.response.ImmutableGameState.builder()
            .oid(request.oid())
            .gameId(wsContext.gameId())
            .currentState(state)
            .build();
        bus.responseWithMessage(response);
      }

      case get_players_state -> {
        final var playersStatus = playerStateService.getPlayersState(wsContext.gameId(), wsContext.ruleset().players());
        final var response = dev.maizy.myna.game_message.response.ImmutablePlayersState.builder()
            .gameId(wsContext.gameId())
            .oid(request.oid())
            .players(playersStatus)
            .build();
        bus.responseWithMessage(response);
      }

      default ->
        log.warn("unsupported request type: {}", request.requestType());
    }
  }


  public void onPlayerConnected(PlayerWebsocketContext wsContext, GameMessageBus bus) {
    wsContext.rulesetPlayer().ifPresent(player -> {
      playerStateService.setPlayerConnected(wsContext.gameId(), player.id(), wsContext.uid());
      final var message = ImmutablePlayerState.builder()
          .player(player)
          .gameId(wsContext.gameId())
          .eventType(EventType.player_connected)
          .build();
      bus.broadcastMessageToAllPlayersExceptCurrent(message);
    });

    final var playersStatus = playerStateService.getPlayersState(wsContext.gameId(), wsContext.ruleset().players());
    final var event = ImmutablePlayersState.builder()
        .gameId(wsContext.gameId())
        .players(playersStatus)
        .build();

    bus.broadcastMessageToAllPlayers(event);
  }

  public void onPlayerDisconnected(PlayerWebsocketContext wsContext, GameMessageBus bus) {
    wsContext.rulesetPlayer().ifPresent(player -> {
      playerStateService.setPlayerDisconnected(wsContext.gameId(), player.id());
      final var message = ImmutablePlayerState.builder()
          .player(player)
          .gameId(wsContext.gameId())
          .eventType(EventType.player_disconnected)
          .build();
      bus.broadcastMessageToAllPlayersExceptCurrent(message);
    });

    final var playersStatus = playerStateService.getPlayersState(wsContext.gameId(), wsContext.ruleset().players());
    final var event = ImmutablePlayersState.builder()
        .gameId(wsContext.gameId())
        .players(playersStatus)
        .build();

    bus.broadcastMessageToAllPlayers(event);
  }

  public void onGameStateChange(
      GameStateContext gameStateContext,
      GameState previousState,
      GameState newState,
      GameMessageBus bus) {
    final var gameStateMessage = ImmutableGameState.builder()
        .gameId(gameStateContext.gameId())
        .previousState(previousState)
        .newState(newState)
        .build();
    bus.broadcastMessageToAllPlayers(gameStateMessage);
  }

  public void onBroadcastEventReceived(BroadcastMessage event, ConnectedPlayers connectedPlayers) {
    if (event.exceptWsId() != null) {
      connectedPlayers.sendMessageToAllConnectedPlayersExcept(event.exceptWsId(), event.message());
    } else {
      connectedPlayers.sendMessageToAllConnectedPlayers(event.message());
    }
  }
}
