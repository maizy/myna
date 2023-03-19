package dev.maizy.myna.dto.custom_serializer;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.maizy.myna.ruleset.Ref;
import java.io.IOException;

public class RefSerializer extends StdSerializer<Ref> {

  public RefSerializer() {
    this(null);
  }

  public RefSerializer(Class<Ref> t) {
    super(t);
  }

  @Override
  public void serialize(Ref value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeString(value.id());
  }
}
