package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.maizy.myna.game_message.event.ImmutableGameState;
import dev.maizy.myna.game_message.event.ImmutableObjectDrag;
import dev.maizy.myna.game_message.event.ImmutableObjectMove;
import dev.maizy.myna.game_message.event.ImmutablePlayerState;
import dev.maizy.myna.game_message.event.ImmutablePlayersState;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "eventType",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ImmutablePlayersState.class, name = "players_state_changed"),
    @JsonSubTypes.Type(value = ImmutablePlayerState.class, names = {"player_connected", "player_disconnected"}),
    @JsonSubTypes.Type(value = ImmutableGameState.class, names = {"game_state_changed"}),
    @JsonSubTypes.Type(value = ImmutableObjectDrag.class, names = {"object_drag"}),
    @JsonSubTypes.Type(value = ImmutableObjectMove.class, names = {"object_move"}),
})
public interface Event extends Message {
  @JsonProperty
  @Override
  default MessageKind kind() {
    return MessageKind.event;
  }

  @JsonProperty
  EventType eventType();
}
