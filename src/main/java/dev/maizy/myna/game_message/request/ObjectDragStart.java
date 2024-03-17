package dev.maizy.myna.game_message.request;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Request;
import dev.maizy.myna.game_message.RequestType;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableObjectDragStart.class)
@JsonDeserialize(as = ImmutableObjectDragStart.class)
public abstract class ObjectDragStart implements Request {

  @Override
  public abstract String gameId();

  public RequestType requestType() {
    return RequestType.object_drag_start;
  }

  public abstract String itemId();

  public abstract String rulesetObjectId();

  public abstract String dragZoneItemId();

}
