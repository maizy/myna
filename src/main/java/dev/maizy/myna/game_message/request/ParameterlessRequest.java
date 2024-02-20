package dev.maizy.myna.game_message.request;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Request;
import dev.maizy.myna.game_message.RequestType;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableParameterlessRequest.class)
@JsonDeserialize(as = ImmutableParameterlessRequest.class)
public abstract class ParameterlessRequest implements Request {

  @Override
  public abstract String gameId();

  public abstract RequestType requestType();

  public abstract Optional<String> oid();
}
