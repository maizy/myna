package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.maizy.myna.game_message.response.FullView;
import dev.maizy.myna.game_message.response.ImmutableWhoYouAre;
import java.util.Optional;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "responseType",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ImmutableWhoYouAre.class, name = "who_you_are"),
    @JsonSubTypes.Type(value = FullView.class, name = "full_view"),
})
public interface Response extends Message {

  @JsonProperty
  @Override
  default MessageKind kind() {
    return MessageKind.response;
  }

  @JsonProperty
  ResponseType responseType();

  @JsonProperty
  Optional<String> oid();
}
