package dev.maizy.myna.dto.custom_serializer;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.maizy.myna.ruleset.ImmutableRef;
import dev.maizy.myna.ruleset.Ref;
import java.io.IOException;

public class RefDeserializer extends StdDeserializer<Ref> {
  public RefDeserializer() {
    this(null);
  }

  public RefDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Ref deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return ImmutableRef.of(p.getText());
  }
}
