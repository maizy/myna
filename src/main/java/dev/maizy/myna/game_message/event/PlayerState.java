package dev.maizy.myna.game_message.event;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Event;
import dev.maizy.myna.game_message.EventType;
import dev.maizy.myna.ruleset.Player;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayerState.class)
@JsonDeserialize(as = ImmutablePlayerState.class)
public abstract class PlayerState implements Event {

  @Override
  public abstract String gameId();

  @Override
  public abstract EventType eventType();

  public abstract Player player();

  @Value.Check
  protected void check() {
    if (eventType() != EventType.player_connected && eventType() != EventType.player_disconnected) {
      throw new IllegalArgumentException("only player_* event allowed");
    }
  }
}
