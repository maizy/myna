package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Message;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableRedisBusMessage.class)
@JsonDeserialize(as = ImmutableRedisBusMessage.class)
public abstract class RedisBusMessage {

  @Nullable
  @Value.Default
  public Long exceptWsId() {
    return null;
  }

  public abstract Message message();
}
