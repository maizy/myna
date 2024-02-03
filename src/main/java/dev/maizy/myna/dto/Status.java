package dev.maizy.myna.dto;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableStatus.class)
@JsonDeserialize(as = ImmutableStatus.class)
public abstract class Status {
  @Value.Parameter
  public abstract String status();
}
