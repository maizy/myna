package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.auth.AutoGenerateUidFilter;
import dev.maizy.myna.service.game_events.GameEventsSubscriptionService;
import dev.maizy.myna.ws.GameWebsocketHandler;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class GameWebsocketConfiguration implements WebSocketConfigurer {

  private final GameEventsSubscriptionService gameEventsSubscriptionService;

  public GameWebsocketConfiguration(@Autowired GameEventsSubscriptionService gameEventsSubscriptionService) {
    this.gameEventsSubscriptionService = gameEventsSubscriptionService;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(new GameWebsocketHandler(gameEventsSubscriptionService), "/ws/game")
        .addInterceptors(
            new HttpSessionHandshakeInterceptor(Collections.singletonList(AutoGenerateUidFilter.UID_SESSION_KEY))
        );
  }

}
