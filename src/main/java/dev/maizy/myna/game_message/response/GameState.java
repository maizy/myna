package dev.maizy.myna.game_message.response;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Response;
import dev.maizy.myna.game_message.ResponseType;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGameState.class)
@JsonDeserialize(as = ImmutableGameState.class)
@JsonTypeName("game_state")
public abstract class GameState implements Response {

  @Override
  public ResponseType responseType() {
    return ResponseType.game_state;
  }

  @Override
  public abstract String gameId();

  @Override
  public abstract Optional<String> oid();

  public abstract dev.maizy.myna.game_state.GameState currentState();
}
