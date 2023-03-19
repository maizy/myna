package dev.maizy.myna.surface;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

@Value.Immutable
@JsonSerialize(as = ImmutablePoint.class)
@JsonDeserialize(as = ImmutablePoint.class)
public abstract class Point {

  @Value.Parameter
  public abstract int x();

  @Value.Parameter
  public abstract int y();
}
