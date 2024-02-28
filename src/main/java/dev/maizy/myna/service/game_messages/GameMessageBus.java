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
public class GameMessageBus {
  private static final Logger log = LoggerFactory.getLogger(GameMessageBus.class);

  private final Long currentPlayerWsId;
  private final String gameId;

  private GameMessagesSubscriptionService gameMessagesSubscriptionService;
  private GameBroadcastMessagesPublisher gameBroadcastMessagesPublisher;
  private ObjectMapper objectMapper;

  public GameMessageBus(Long currentPlayerWsId, String gameId) {
    this.currentPlayerWsId = currentPlayerWsId;
    this.gameId = gameId;
  }

  @Autowired
  protected void setGameMessagesSubscriptionService(GameMessagesSubscriptionService gameMessagesSubscriptionService) {
    this.gameMessagesSubscriptionService = gameMessagesSubscriptionService;
  }

  @Autowired
  protected void setGameBroadcastMessagesPublisher(GameBroadcastMessagesPublisher gameBroadcastMessagesPublisher) {
    this.gameBroadcastMessagesPublisher = gameBroadcastMessagesPublisher;
  }

  @Autowired
  protected void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void broadcastMessageToAllPlayersExceptCurrent(Message message) {
    gameBroadcastMessagesPublisher.publishGameMessage(gameId, currentPlayerWsId, message);
  }

  public void broadcastMessageToAllPlayers(Message message) {
    gameBroadcastMessagesPublisher.publishGameMessage(gameId, null, message);
  }

  public void responseWithMessage(Message message) {
    if (currentPlayerWsId != null) {
      final TextMessage wsMessage;
      try {
        wsMessage = new TextMessage(objectMapper.writeValueAsBytes(message));
      } catch (JsonProcessingException e) {
        log.warn("Unable to form a websocket message for response, gameId={}: {}", gameId, e.getOriginalMessage());
        return;
      }
      gameMessagesSubscriptionService.sendMessageToConnectedPlayer(gameId, currentPlayerWsId, wsMessage);
    } else {
      log.warn("Trying to respond without a websocket connection, gameId={}", gameId);
    }
  }

}
