package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.game_message.BroadcastMessage;
import dev.maizy.myna.game_message.EventType;
import dev.maizy.myna.game_message.PlayerMessage;
import dev.maizy.myna.game_message.event.ImmutableGameState;
import dev.maizy.myna.game_message.event.ImmutablePlayerState;
import dev.maizy.myna.game_message.event.ImmutablePlayerWithStatus;
import dev.maizy.myna.game_message.event.ImmutablePlayersState;
import dev.maizy.myna.game_message.event.PlayersState;
import dev.maizy.myna.game_state.GameState;
import dev.maizy.myna.ruleset.ImmutablePlayer;
import dev.maizy.myna.ws.PlayerWebsocketContext;
import org.springframework.stereotype.Service;

@Service
public class GameMessageHandler {

  public void onPlayerMessageReceived(PlayerMessage event, GameMessageBus bus) {
  }

  public void onPlayerConnected(PlayerWebsocketContext wsContext, GameMessageBus bus) {
    wsContext.rulesetPlayer().ifPresent(player -> {
      final var message = ImmutablePlayerState.builder()
          .player(player)
          .gameId(wsContext.gameId())
          .eventType(EventType.player_connected)
          .build();
      bus.broadcastMessageToAllPlayersExceptCurrent(message);
    });

    bus.broadcastMessageToAllPlayers(getCurrentPlayersState(wsContext.gameId()));
  }

  public void onPlayerDisconnected(PlayerWebsocketContext wsContext, GameMessageBus bus) {
    wsContext.rulesetPlayer().ifPresent(player -> {
      final var message = ImmutablePlayerState.builder()
          .player(player)
          .gameId(wsContext.gameId())
          .eventType(EventType.player_disconnected)
          .build();
      bus.broadcastMessageToAllPlayersExceptCurrent(message);
    });

     bus.broadcastMessageToAllPlayers(getCurrentPlayersState(wsContext.gameId()));
  }

  /**
   * FIXME temp
   */
  private PlayersState getCurrentPlayersState(String gameId) {
    return ImmutablePlayersState.builder()
        .gameId(gameId)
        .addPlayer(ImmutablePlayerWithStatus.of(ImmutablePlayer.of("master"), PlayersState.PlayerStatus.playing))
        .addPlayer(ImmutablePlayerWithStatus.of(ImmutablePlayer.of("p1"), PlayersState.PlayerStatus.playing))
        .addPlayer(ImmutablePlayerWithStatus.of(ImmutablePlayer.of("p2"), PlayersState.PlayerStatus.absent))
        .addPlayer(ImmutablePlayerWithStatus.of(ImmutablePlayer.of("p3"), PlayersState.PlayerStatus.playing))
        .build();
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
