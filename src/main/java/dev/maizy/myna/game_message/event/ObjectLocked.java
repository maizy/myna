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

@Value.Immutable
@JsonSerialize(as = ImmutableObjectLocked.class)
@JsonDeserialize(as = ImmutableObjectLocked.class)
public abstract class ObjectLocked implements Event {

  @Override
  public abstract String gameId();

  @Override
  public EventType eventType() {
    return EventType.object_locked;
  }

  public abstract String itemId();

  public abstract String rulesetObjectId();

}
