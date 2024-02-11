package dev.maizy.myna.ws;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.service.game_events.GameEventsSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GameWebsocketHandler extends TextWebSocketHandler {
  private static final Logger log = LoggerFactory.getLogger(GameWebsocketHandler.class);

  private final GameEventsSubscriptionService gameEventsSubscriptionService;
  private Long subscriptionId = null;

  public GameWebsocketHandler(GameEventsSubscriptionService gameEventsSubscriptionService) {
    this.gameEventsSubscriptionService = gameEventsSubscriptionService;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    final var uidAttr = session.getAttributes().get("uid");
    if (uidAttr instanceof String uid) {
      session.sendMessage(new TextMessage("Hi! your uid: " + uid));
      subscriptionId = gameEventsSubscriptionService.addPlayerWsSession("test", "r1", session);
    } else {
      log.error("Unable to get uid for websocket connection");
      session.close();
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    if (subscriptionId != null) {
      gameEventsSubscriptionService.removePlayerWsSession("test", subscriptionId);
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.info("message: {}", message);
    log.info("session: {}", session);
    session.sendMessage(new TextMessage("answer " + message.getPayload()));
  }
}
