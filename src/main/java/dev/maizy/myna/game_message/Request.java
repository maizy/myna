package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.maizy.myna.game_message.request.ImmutableParameterlessRequest;
import java.util.Optional;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "requestType",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = ImmutableParameterlessRequest.class,
        names = {"who_am_i", "get_full_view", "get_game_state", "get_players_state"}
    ),
})
public interface Request extends Message {

  @JsonProperty
  @Override
  default MessageKind kind() {
    return MessageKind.request;
  }

  @JsonProperty
  RequestType requestType();

  @JsonProperty
  Optional<String> oid();
}
