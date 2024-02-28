package dev.maizy.myna.game_message;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ValueNode;
import java.io.IOException;

public class MessageJsonDeserializer extends StdDeserializer<Message> {

  public MessageJsonDeserializer() {
    this(null);
  }

  public MessageJsonDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Message deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
    final var codec = jsonParser.getCodec();
    final var tree = codec.readTree(jsonParser);
    final var kind = tree.get("kind");
    if (kind instanceof ValueNode valKind) {
      final var kindValue = valKind.asText();
      final MessageKind kindEnum;
      try {
        kindEnum = MessageKind.valueOf(kindValue);
      } catch (IllegalArgumentException e) {
        throw new JsonParseException(jsonParser, "unknown message kind '" + kindValue + "'");
      }
      return switch (kindEnum) {
        case event -> codec.treeToValue(tree, Event.class);
        case request -> codec.treeToValue(tree, Request.class);
        case response -> codec.treeToValue(tree, Response.class);
      };
    } else {
      throw new JsonParseException(jsonParser, "kind should be JSON String");
    }
  }
}
