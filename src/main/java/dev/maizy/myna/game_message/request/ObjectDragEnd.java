package dev.maizy.myna.game_message.request;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Request;
import dev.maizy.myna.game_message.RequestType;
import dev.maizy.myna.surface.Point;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableObjectDragEnd.class)
@JsonDeserialize(as = ImmutableObjectDragEnd.class)
public abstract class ObjectDragEnd implements Request {

  @Override
  public abstract String gameId();

  public RequestType requestType() {
    return RequestType.object_drag_end;
  }

  public abstract String itemId();

  public abstract String rulesetObjectId();

  public abstract String dragZoneItemId();

  public abstract Point position();

}
