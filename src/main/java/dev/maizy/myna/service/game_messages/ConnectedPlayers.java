package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.maizy.myna.game_message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
@Scope(SCOPE_PROTOTYPE)
public class ConnectedPlayers {
  private static final Logger log = LoggerFactory.getLogger(ConnectedPlayers.class);

  private final String gameId;

  private GameMessagesSubscriptionService gameMessagesSubscriptionService;
  private ObjectMapper objectMapper;

  public ConnectedPlayers(String gameId) {
    this.gameId = gameId;
  }

  @Autowired
  protected void setGameMessagesSubscriptionService(GameMessagesSubscriptionService gameMessagesSubscriptionService) {
    this.gameMessagesSubscriptionService = gameMessagesSubscriptionService;
  }

  @Autowired
  protected void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private TextMessage serializeMessage(Message message) throws JsonProcessingException {
    return new TextMessage(objectMapper.writeValueAsBytes(message));
  }

  public void sendMessageToAllConnectedPlayers(Message message) {
    final TextMessage wsMessage;
    try {
      wsMessage = serializeMessage(message);
    } catch (JsonProcessingException e) {
      log.error("Unable to serialize websocket message, gameId={}: {}", gameId, e.getOriginalMessage());
      return;
    }
    gameMessagesSubscriptionService.sendMessageToAllConnectedPlayers(gameId, wsMessage);
  }

  public void sendMessageToAllConnectedPlayersExcept(long wsId, Message message) {
    final TextMessage wsMessage;
    try {
      wsMessage = serializeMessage(message);
    } catch (JsonProcessingException e) {
      log.error(
          "Unable to serialize websocket message, gameId={}, exceptWsId={}: {}",
          gameId, wsId, e.getOriginalMessage()
      );
      return;
    }
    gameMessagesSubscriptionService.sendMessageToAllConnectedPlayersExcept(gameId, wsId, wsMessage);
  }
}
