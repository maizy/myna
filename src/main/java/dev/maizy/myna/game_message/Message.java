package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "kind",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Event.class, name = "event"),
    @JsonSubTypes.Type(value = Request.class, name = "request"),
    @JsonSubTypes.Type(value = Response.class, name = "response"),
})
@JsonDeserialize(using = MessageJsonDeserializer.class)
public interface Message {
  @JsonProperty
  MessageKind kind();

  @JsonProperty
  String gameId();
}
