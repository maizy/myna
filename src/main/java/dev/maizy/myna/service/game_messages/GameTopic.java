package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.service.RedisKeys;
import java.util.Objects;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

public final class GameTopic {
  public static Topic forGame(String gameId) {
    Objects.requireNonNull(gameId);
    return new ChannelTopic(RedisKeys.getMessagesKey(gameId));
  }

  private GameTopic() {
  }
}
