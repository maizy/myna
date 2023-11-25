package dev.maizy.myna.ruleset;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.dto.custom_serializer.RefDeserializer;
import dev.maizy.myna.dto.custom_serializer.RefSerializer;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(using = RefSerializer.class)
@JsonDeserialize(using = RefDeserializer.class)
public abstract class Ref {
  @Value.Parameter
  public abstract String id();
}
