package dev.maizy.myna.dto.api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableApiRoot.class)
@JsonDeserialize(as = ImmutableApiRoot.class)
public abstract class ApiRoot {
  public abstract String version();
  public abstract List<ApiObject> objects();
}
