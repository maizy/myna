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
@JsonSerialize(as = ImmutableObjectMove.class)
@JsonDeserialize(as = ImmutableObjectMove.class)
public abstract class ObjectMove implements Event {

  @Override
  public abstract String gameId();

  @Override
  public EventType eventType() {
    return EventType.object_move;
  }

  public abstract String itemId();

  public abstract String rulesetObjectId();

  public abstract Point position();

}
