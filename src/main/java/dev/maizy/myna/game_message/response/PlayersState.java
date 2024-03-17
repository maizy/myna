package dev.maizy.myna.game_message.response;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.PlayerWithStatus;
import dev.maizy.myna.game_message.Response;
import dev.maizy.myna.game_message.ResponseType;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayersState.class)
@JsonDeserialize(as = ImmutablePlayersState.class)
public abstract class PlayersState implements Response {

  @Override
  public ResponseType responseType() {
    return ResponseType.players_state;
  }

  @Override
  public abstract String gameId();

  @Override
  public abstract Optional<String> oid();

  public abstract List<PlayerWithStatus> players();
}
