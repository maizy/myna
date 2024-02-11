package dev.maizy.myna.service.game_events;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

@Service
public class GameEventsSubscriptionService implements ConnectedPlayerDispatcher {

  private static final Logger log = LoggerFactory.getLogger(GameEventsSubscriptionService.class);

  private final RedisMessageListenerContainer redisListenerContainer;
  private final Random random = new SecureRandom();

  /**
   * subscriptions for game events, only need to handle subscriptions to game with active players
   */
  final private ConcurrentMap<String, GameEventsListiner> gameEventsSubscriptions = new ConcurrentHashMap<>();

  private record PlayerWsSession(long id, String rolesetPlayerId, ConcurrentWebSocketSessionDecorator wsSession){}
  final private ConcurrentMap<String, ConcurrentMap<Long, PlayerWsSession>> playersWsSessions =
      new ConcurrentHashMap<>();

  public GameEventsSubscriptionService(@Autowired RedisMessageListenerContainer redisMessageListenerContainer) {
    this.redisListenerContainer = redisMessageListenerContainer;
  }

  @Override
  public void broadcastMessageToConnectedPlayers(String gameId, String body) {
    final var players = playersWsSessions.get(gameId);
    if (players != null && !players.isEmpty()) {
      final var message = new TextMessage(body);
      for (final var player : players.values()) {
        try {
          player.wsSession.sendMessage(message);
        } catch (IOException e) {
          log.warn("Unable to deliver message to player: gameId={}, playerId={}", gameId, player.rolesetPlayerId());
        }
      }
    }
  }

  private void subscribeForGameEvents(String gameId) {
    gameEventsSubscriptions.computeIfAbsent(
        gameId,
        newGameId -> {
          final var gameEventsListiner = new GameEventsListiner(newGameId, this);
          redisListenerContainer.addMessageListener(
              gameEventsListiner, GameTopic.forGame(newGameId)
          );
          log.debug("subscribe to game events for gameId={}", newGameId);
          return gameEventsListiner;
        }
    );
  }

  private void unsubscribeFromGameEvents(String gameId) {
    gameEventsSubscriptions.computeIfPresent(gameId, (removedGameId, listiner) -> {
      redisListenerContainer.removeMessageListener(listiner);
      log.debug("unsubscribe from game events for gameId={}", removedGameId);
      return null;
    });
  }

  public long addPlayerWsSession(String gameId, String rolesetPlayerId, WebSocketSession wsSession) {
    final var wsId = random.nextLong();
    final var wsSessionDecorator = new ConcurrentWebSocketSessionDecorator(
        wsSession, 1000, 512, ConcurrentWebSocketSessionDecorator.OverflowStrategy.DROP
    );
    final var playerWsSession = new PlayerWsSession(wsId, rolesetPlayerId, wsSessionDecorator);
    playersWsSessions.compute(gameId, (newGameId, sessions) -> {
      final var nonEmptySessions = (sessions == null) ? new ConcurrentHashMap<Long, PlayerWsSession>() : sessions;
      nonEmptySessions.put(wsId, playerWsSession);
      log.debug("player subscribed to gameId={}, {} players in total", gameId, nonEmptySessions.size());
      return nonEmptySessions;
    });

    if (!gameEventsSubscriptions.containsKey(gameId)) {
      subscribeForGameEvents(gameId);
    }
    return wsId;
  }

  public void removePlayerWsSession(String gameId, long wsSessionId) {
    playersWsSessions.compute(gameId, (thisGameId, sessions) -> {
      if (sessions == null) {
        return null;
      }
      sessions.remove(wsSessionId);
      log.debug("player unsubscribed from gameId={}, {} players remaining", gameId, sessions.size());
      if (sessions.isEmpty()) {
        unsubscribeFromGameEvents(thisGameId);
        return null;
      }
      return sessions;
    });
  }

}
