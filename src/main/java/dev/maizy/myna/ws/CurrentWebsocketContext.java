package dev.maizy.myna.ws;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.io.IOException;
import org.springframework.web.socket.WebSocketSession;

public class CurrentWebsocketContext {

  public static WebsocketContext getWebsocketContext(WebSocketSession wsSession) {
    final var attr = wsSession.getAttributes().get(GameAccessWebsocketInterceptor.contextKey);
    if (attr instanceof WebsocketContext context) {
      return context;
    }
    try {
      wsSession.close();
    } catch (IOException e) {
    }
    throw new IllegalArgumentException("Game websocket context not found");
  }
}
