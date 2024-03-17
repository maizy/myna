package dev.maizy.myna.game_message.event;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Event;
import dev.maizy.myna.game_message.EventType;
import dev.maizy.myna.surface.Point;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableObjectDrag.class)
@JsonDeserialize(as = ImmutableObjectDrag.class)
public abstract class ObjectDrag implements Event {

  @Override
  public abstract String gameId();

  @Override
  public EventType eventType() {
    return EventType.object_drag;
  }

  public abstract String itemId();

  public abstract String rulesetObjectId();

  public abstract Point position();

}
