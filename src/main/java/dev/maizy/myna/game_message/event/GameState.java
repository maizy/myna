package dev.maizy.myna.game_message.event;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Event;
import dev.maizy.myna.game_message.EventType;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableGameState.class)
@JsonDeserialize(as = ImmutableGameState.class)
public abstract class GameState implements Event {

  @Override
  public abstract String gameId();

  @Override
  public EventType eventType() {
    return EventType.game_state_changed;
  }

  @Nullable
  public abstract dev.maizy.myna.game_state.GameState previousState();

  public abstract dev.maizy.myna.game_state.GameState newState();
}
