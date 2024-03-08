package dev.maizy.myna.game_model;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.surface.Rectangle;
import dev.maizy.myna.surface.RectangleAppearance;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGameObject.class)
@JsonDeserialize(as = ImmutableGameObject.class)
public abstract class GameObject implements Item {

  @JsonProperty("zIndex")
  public abstract int zIndex();

  public abstract String itemId();

  public abstract String rulesetObjectId();

  public abstract RectangleAppearance currentAppereance();

  public abstract Rectangle currentPosition();

  public abstract Ref container();

  @Value.Default
  @JsonProperty("isDraggable")
  public boolean isDraggable() {
    return false;
  }

  @Value.Default
  public Optional<Ref> draggableZone() {
    return isDraggable() ? Optional.of(container()) : Optional.empty();
  }

  @Value.Check
  protected void check() {
    if (isDraggable() && draggableZone().isEmpty()) {
      throw new IllegalArgumentException("draggable zone should be defined if object is draggable");
    }
  }
}
