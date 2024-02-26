package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

@Service
public class GameMessagesSubscriptionService {

  public record PlayerWsSession(long id, String rolesetPlayerId, ConcurrentWebSocketSessionDecorator wsSession) {
  }

  private static final Logger log = LoggerFactory.getLogger(GameMessagesSubscriptionService.class);

  private final RedisMessageListenerContainer redisListenerContainer;
  private final ObjectProvider<GameBroadcastMessagesListener> gameBroadcastMessagesListenerFactory;

  private final Random random = new SecureRandom();

  /**
   * subscriptions for game messages, only need to handle subscriptions to game with active players
   */
  private final ConcurrentMap<String, GameBroadcastMessagesListener> gamesBroadcastMessagesListeners =
      new ConcurrentHashMap<>();

  private final ConcurrentMap<String, ConcurrentMap<Long, PlayerWsSession>> playersWsSessions =
      new ConcurrentHashMap<>();

  public GameMessagesSubscriptionService(
      RedisMessageListenerContainer redisListenerContainer,
      ObjectProvider<GameBroadcastMessagesListener> gameBroadcastMessagesListenerFactory) {
    this.redisListenerContainer = redisListenerContainer;
    this.gameBroadcastMessagesListenerFactory = gameBroadcastMessagesListenerFactory;
  }

  private void subscribeForGameMessages(String gameId) {
    gamesBroadcastMessagesListeners.computeIfAbsent(
        gameId,
        newGameId -> {
          final var gameMessagesListiner = gameBroadcastMessagesListenerFactory.getObject(newGameId);
          redisListenerContainer.addMessageListener(
              gameMessagesListiner, GameTopic.forGame(newGameId)
          );
          log.debug("subscribe to game messages for gameId={}", newGameId);
          return gameMessagesListiner;
        }
    );
  }

  private void unsubscribeFromGameMessages(String gameId) {
    gamesBroadcastMessagesListeners.computeIfPresent(gameId, (removedGameId, listiner) -> {
      redisListenerContainer.removeMessageListener(listiner);
      log.debug("unsubscribe from game messages for gameId={}", removedGameId);
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

    if (!gamesBroadcastMessagesListeners.containsKey(gameId)) {
      subscribeForGameMessages(gameId);
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
        unsubscribeFromGameMessages(thisGameId);
        return null;
      }
      return sessions;
    });
  }

  public void sendMessageToConnectedPlayer(String gameId, long wsId, WebSocketMessage<?> message) {
    final var gamePlayers = playersWsSessions.get(gameId);
    if (gamePlayers != null && !gamePlayers.isEmpty()) {
      final var ws = gamePlayers.get(wsId);
      if (ws != null) {
        try {
          ws.wsSession.sendMessage(message);
        } catch (IOException e) {
          log.error("error while sending a message to a player: gameId={}, wsId={}: {}", gameId, wsId, e);
        }
      }
    }
  }

  public void sendMessageToAllConnectedPlayers(String gameId, WebSocketMessage<?> message) {
    final var players = getConnectedPlayersIterator(gameId, null);
    sendToPlayersFromIterator(players, gameId, message);
  }

  public void sendMessageToAllConnectedPlayersExcept(String gameId, long wsId, WebSocketMessage<?> message) {
    final var players = getConnectedPlayersIterator(gameId, wsId);
    sendToPlayersFromIterator(players, gameId, message);
  }

  private Iterator<PlayerWsSession> getConnectedPlayersIterator(String gameId, Long excludeWsId) {
    final var players = playersWsSessions.get(gameId);
    if (players == null || players.isEmpty()) {
      return Collections.emptyIterator();
    } else if (excludeWsId != null) {
      return players.values().stream().filter(p -> p.id != excludeWsId).iterator();
    } else {
      return players.values().iterator();
    }
  }

  private void sendToPlayersFromIterator(
      Iterator<PlayerWsSession> playersIt, String gameId, WebSocketMessage<?> message) {
    while (playersIt.hasNext()) {
      final var player = playersIt.next();
      try {
        player.wsSession().sendMessage(message);
      } catch (IOException e) {
        log.warn(
            "Unable to deliver broadcast message to player: gameId={}, playerId={}",
            gameId, player.rolesetPlayerId()
        );
      }
    }
  }
}
