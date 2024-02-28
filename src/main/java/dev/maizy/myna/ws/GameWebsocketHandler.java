package dev.maizy.myna.ws;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.maizy.myna.game_message.Message;
import dev.maizy.myna.game_message.PlayerMessage;
import dev.maizy.myna.service.game_messages.GameMessageBus;
import dev.maizy.myna.service.game_messages.GameMessageHandler;
import dev.maizy.myna.service.game_messages.GameMessagesSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
public class GameWebsocketHandler extends TextWebSocketHandler {
  private static final Logger log = LoggerFactory.getLogger(GameWebsocketHandler.class);
  private static final String playerContextKey = "playerContext";

  private final GameMessagesSubscriptionService gameMessagesSubscriptionService;
  private final GameMessageHandler gameMessageHandler;
  private final ObjectMapper objectMapper;
  private final ObjectProvider<GameMessageBus> gameMessageBusFactory;

  public GameWebsocketHandler(
      GameMessagesSubscriptionService gameMessagesSubscriptionService,
      GameMessageHandler gameMessageHandler,
      ObjectMapper objectMapper,
      ObjectProvider<GameMessageBus> gameMessageBusFactory
  ) {
    this.gameMessagesSubscriptionService = gameMessagesSubscriptionService;
    this.gameMessageHandler = gameMessageHandler;
    this.objectMapper = objectMapper;
    this.gameMessageBusFactory = gameMessageBusFactory;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    final var context = CurrentWebsocketContext.getWebsocketContext(session);
    final var wsId = gameMessagesSubscriptionService.addPlayerWsSession(
        context.getGameId(), context.getRulesetPlayerId().orElse(null), session
    );
    final var playerContext = new PlayerWebsocketContext(
        wsId, context.getGameId(), context.getRuleset(), context.getUid(), context.getRulesetPlayer()
    );
    session.getAttributes().put(playerContextKey, playerContext);

    final var bus = gameMessageBusFactory.getObject(wsId, playerContext.gameId());
    gameMessageHandler.onPlayerConnected(playerContext, bus);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    if (session.getAttributes().get(playerContextKey) instanceof PlayerWebsocketContext playerContext) {
      gameMessagesSubscriptionService.removePlayerWsSession(playerContext.gameId(), playerContext.wsId());

      final var bus = gameMessageBusFactory.getObject(playerContext.wsId(), playerContext.gameId());
      gameMessageHandler.onPlayerDisconnected(playerContext, bus);
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage wsMessage) {
    if (session.getAttributes().get(playerContextKey) instanceof PlayerWebsocketContext playerContext) {
      final var payload = wsMessage.getPayload();
      if (log.isTraceEnabled()) {
        log.trace("incomming websocket message, gameId={}: {}", playerContext.gameId(), payload);
      }

      final Message message;
      try {
        message = objectMapper.readValue(payload, Message.class);
      } catch (JsonProcessingException e) {
        log.error(
            "Unable to deserialize websocket message, gameId={}: {}",
            playerContext.gameId(), e.getOriginalMessage()
        );
        return;
      }

      final var bus = gameMessageBusFactory.getObject(playerContext.wsId(), playerContext.gameId());
      final var playerEvent = new PlayerMessage(playerContext, message);
      gameMessageHandler.onPlayerMessageReceived(playerEvent, bus);
    } else {
      log.warn("Receive text message before building websocket player context");
    }
  }
}
