package dev.maizy.myna.surface;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableRectangleAppearance.class)
@JsonDeserialize(as = ImmutableRectangleAppearance.class)
public abstract class RectangleAppearance {
  public abstract Optional<String> text();

  @Value.Default
  public Color textColor() {
    return Colors.black.hex();
  }

  public abstract Optional<Color> backgroundColor();
  public abstract Optional<String> backgroundImage();
}
