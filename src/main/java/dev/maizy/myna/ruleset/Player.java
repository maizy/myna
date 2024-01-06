package dev.maizy.myna.ruleset;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayer.class)
@JsonDeserialize(as = ImmutablePlayer.class)
public abstract class Player {
  @Value.Parameter
  public abstract String id();

  @Value.Default
  public String roleName() {
    return id();
  }

  public Ref ref() {
    return ImmutableRef.of(id());
  }
}
