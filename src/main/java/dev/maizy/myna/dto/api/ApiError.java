package dev.maizy.myna.dto.api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableApiError.class)
@JsonDeserialize(as = ImmutableApiError.class)
public abstract class ApiError {
  @Value.Parameter
  public abstract String error();

  @Value.Default
  @Nullable
  @JsonInclude(Include.NON_NULL)
  public String description() {
    return null;
  }
}
