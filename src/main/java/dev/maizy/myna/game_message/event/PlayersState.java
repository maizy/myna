package dev.maizy.myna.game_message.event;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Event;
import dev.maizy.myna.game_message.EventType;
import dev.maizy.myna.ruleset.Player;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayersState.class)
@JsonDeserialize(as = ImmutablePlayersState.class)
public abstract class PlayersState implements Event {

  public enum PlayerStatus { played, absent }

  @Value.Immutable
  @JsonSerialize(as = ImmutablePlayerWithStatus.class)
  @JsonDeserialize(as = ImmutablePlayerWithStatus.class)
  public abstract static class PlayerWithStatus {
    @Value.Parameter
    public abstract Player player();
    @Value.Parameter
    public abstract PlayerStatus status();
  }

  @Override
  public abstract String gameId();

  public EventType eventType() {
    return EventType.players_state_changed;
  }

  public abstract List<PlayerWithStatus> players();
}
