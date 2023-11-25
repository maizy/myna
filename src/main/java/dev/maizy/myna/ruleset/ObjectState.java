package dev.maizy.myna.ruleset;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.surface.RectangleAppearance;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableObjectState.class)
@JsonDeserialize(as = ImmutableObjectState.class)
public abstract class ObjectState {
  @Value.Default
  public String id() {
    return UUID.randomUUID().toString();
  }
  public abstract RectangleAppearance appearance();
}
