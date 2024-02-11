package dev.maizy.myna.service.game_events;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public record GameEventsListiner(
    String gameId,
    ConnectedPlayerDispatcher wsBroadcastDispatcher
) implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(GameEventsListiner.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
      // FIXME deserialize, add handler
      final var body = new String(message.getBody(), StandardCharsets.UTF_8);
      log.info("receive message for gameId {}: {}", gameId, body);
      wsBroadcastDispatcher.broadcastMessageToConnectedPlayers(gameId, body);
    }
  }
