package dev.maizy.myna.dto.api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

public final class ApiErrors {
  public static final ApiError NotFound = ImmutableApiError.of("not_found");
  public static final ApiError DuplicateId = ImmutableApiError.of("duplicate_id");
  public static final ApiError InvalidData = ImmutableApiError.of("invalid_data");
  public static final ApiError Unable = ImmutableApiError.of("unable");

  public static ApiError unable(String description) {
    return ImmutableApiError.builder().error(Unable.error()).description(description).build();
  }
}
