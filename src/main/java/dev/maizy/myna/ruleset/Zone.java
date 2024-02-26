package dev.maizy.myna.ruleset;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.surface.Colors;
import dev.maizy.myna.surface.ImmutableRectangleAppearance;
import dev.maizy.myna.surface.Rectangle;
import dev.maizy.myna.surface.RectangleAppearance;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableZone.class)
@JsonDeserialize(as = ImmutableZone.class)
public abstract class Zone {
  public abstract Rectangle position();

  @Value.Default
  public RectangleAppearance appearance() {
    return ImmutableRectangleAppearance.builder()
        .backgroundColor(Colors.white.hex())
        .build();
  }

  /**
   * unsupported - <a href="https://github.com/maizy/myna/issues/11">iss #11</a>
   */
  @Value.Default
  public Access access() {
    return ImmutableStaticAccess.builder().visibleForAll(true).ownedForAll(true).build();
  }
}
