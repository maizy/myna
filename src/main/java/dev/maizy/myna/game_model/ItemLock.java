package dev.maizy.myna.game_model;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableItemLock.class)
@JsonDeserialize(as = ImmutableItemLock.class)
public abstract class ItemLock {

  @Value.Parameter
  public abstract long wsId();

  @Value.Parameter
  @Nullable
  public abstract String rulesetPlayerId();
}
