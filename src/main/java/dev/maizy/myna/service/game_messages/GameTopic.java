package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.util.Objects;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

public class GameTopic {
  public static Topic forGame(String gameId) {
    Objects.requireNonNull(gameId);
    return new ChannelTopic("game-messages-" + gameId);
  }
}
