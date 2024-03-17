package dev.maizy.myna.game_message.response;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Response;
import dev.maizy.myna.game_message.ResponseType;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableOk.class)
@JsonDeserialize(as = ImmutableOk.class)
public abstract class Ok implements Response {

  @Override
  public ResponseType responseType() {
    return ResponseType.ok;
  }

  @Override
  public abstract String gameId();

  @Override
  public abstract Optional<String> oid();
}
