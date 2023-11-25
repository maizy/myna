package dev.maizy.myna.ruleset;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableObjectsGroup.class)
@JsonDeserialize(as = ImmutableObjectsGroup.class)
public abstract class ObjectsGroup {
  @Value.Default
  public String id() {
    return UUID.randomUUID().toString();
  }

  public abstract List<GameObject> objects();

  @Value.Default
  public PositioningType positioningType() {
    return PositioningType.stack;
  }
}
