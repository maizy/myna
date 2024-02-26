package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.maizy.myna.game_message.BroadcastMessage;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_PROTOTYPE)
public class GameBroadcastMessagesListener implements MessageListener {
  private static final Logger log = LoggerFactory.getLogger(GameBroadcastMessagesListener.class);

  private final String gameId;

  private GameMessageHandler gameMessageHandler;
  private ObjectMapper objectMapper;
  private ObjectProvider<ConnectedPlayers> connectedPlayersFactory;

  public GameBroadcastMessagesListener(String gameId) {
    this.gameId = gameId;
  }

  @Autowired
  protected void setGameMessageHandler(GameMessageHandler gameMessageHandler) {
    this.gameMessageHandler = gameMessageHandler;
  }

  @Autowired
  protected void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Autowired
  protected void setConnectedPlayersFactory(ObjectProvider<ConnectedPlayers> connectedPlayersFactory) {
    this.connectedPlayersFactory = connectedPlayersFactory;
  }

  @Override
  public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {

    final RedisBusMessage busMessage;
    try {
      busMessage = objectMapper.readValue(message.getBody(), RedisBusMessage.class);
    } catch (IOException e) {
      log.error("Unable to read redis broadcast message, gameId={}: {}", gameId, e.getMessage());
      return;
    }

    final var broadcastEvent = new BroadcastMessage(busMessage.message(), busMessage.exceptWsId());
    gameMessageHandler.onBroadcastEventReceived(broadcastEvent, connectedPlayersFactory.getObject(gameId));
  }
}
