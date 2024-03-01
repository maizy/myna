package dev.maizy.myna.game_model;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.surface.RectangleAppearance;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableZone.class)
@JsonDeserialize(as = ImmutableZone.class)
public abstract class Zone implements ItemId {
  public abstract String itemId();
  public abstract RectangleAppearance appereance();
}
