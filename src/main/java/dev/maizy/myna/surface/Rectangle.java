package dev.maizy.myna.surface;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableRectangle.class)
@JsonDeserialize(as = ImmutableRectangle.class)
public abstract class Rectangle {

  @Value.Parameter
  public abstract Point topLeft();

  @Value.Parameter
  public abstract Point bottomRight();

  @Value.Check
  protected void check() {
    if (topLeft().x() > bottomRight().x() || topLeft().y() > bottomRight().y()) {
      throw new IllegalArgumentException("rectangle should have proper top-left & bottom-right base points");
    }
  }

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public Size size() {
    return ImmutableSize.of(bottomRight().x() - topLeft().x(), bottomRight().y() - topLeft().y());
  }

  public static Rectangle fromTopLeftAndSize(Point topLeft, Size size) {
    return ImmutableRectangle.of(topLeft, ImmutablePoint.of(topLeft.x() + size.width(), topLeft.y() + size.height()));
  }
}
