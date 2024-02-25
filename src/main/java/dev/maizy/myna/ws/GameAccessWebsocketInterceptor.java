package dev.maizy.myna.ws;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.service.GameStateService;
import dev.maizy.myna.service.GameStateServiceErrors;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GameAccessWebsocketInterceptor implements HandshakeInterceptor {

  public static final String contextKey = "websocketContext";

  private static final Logger log = LoggerFactory.getLogger(GameAccessWebsocketInterceptor.class);

  private final GameStateService gameStateService;

  public GameAccessWebsocketInterceptor(GameStateService gameStateService) {
    this.gameStateService = gameStateService;
  }

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) {
    final var uid = (String) attributes.get("uid");
    if (uid == null) {
      log.error("Websocket request without authorization");
      return false;
    }

    final var requestUri = UriComponentsBuilder.fromUri(request.getURI()).build();

    final var gameId = requestUri.getQueryParams().getFirst("gameId");
    final var rulesetPlayerId = requestUri.getQueryParams().getFirst("rulesetPlayerId");
    if (gameId == null) {
      log.error("Websocket request without required gameId query parameter");
      response.setStatusCode(HttpStatus.BAD_REQUEST);
      return false;
    }

    final GameStateService.GameAccessAuth gameAccessAuth;
    try {
      if (rulesetPlayerId == null) {
        gameAccessAuth = gameStateService.checkGameAccessAuthByUid(gameId, uid);
      } else {
        gameAccessAuth = gameStateService.checkGameAccessAuthByUidAndPlayerId(gameId, uid, rulesetPlayerId);
      }
    } catch (GameStateServiceErrors.GameNotFound e) {
      response.setStatusCode(HttpStatus.NOT_FOUND);
      return false;
    } catch (GameStateServiceErrors.Forbidden e) {
      response.setStatusCode(HttpStatus.FORBIDDEN);
      return false;
    }

    final var wsGameContext = new WebsocketContext(uid, gameAccessAuth);
    attributes.put(contextKey, wsGameContext);
    return true;
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
  }
}
