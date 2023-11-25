package dev.maizy.myna.surface;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSize.class)
@JsonDeserialize(as = ImmutableSize.class)
public abstract class Size {

  @Value.Parameter
  public abstract int width();

  @Value.Parameter
  public abstract int height();

  @Value.Check
  protected void check() {
    if (width() < 0 || height() < 0) {
      throw new IllegalArgumentException("width & height should be positive");
    }
  }
}
