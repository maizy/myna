package dev.maizy.myna.game_model;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableRef.class)
@JsonDeserialize(as = ImmutableRef.class)
public abstract class Ref {
  @Value.Parameter
  public abstract String itemId();

  public static Ref fromItem(Item item) {
    return ImmutableRef.of(item.itemId());
  }
}
