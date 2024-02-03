package dev.maizy.myna.dto.html;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGamePlayer.class)
@JsonDeserialize(as = ImmutableGamePlayer.class)
public abstract class GamePlayer {
  @Value.Parameter
  public abstract String id();
  @Value.Parameter
  public abstract String roleName();
}
