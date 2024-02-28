package dev.maizy.myna.dto.custom_serializer;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.PageImpl;

@JsonComponent
public class PageSerializer extends JsonSerializer<PageImpl<?>> {

  @Override
  public void serialize(
      PageImpl page,
      JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("content", page.getContent());
    jsonGenerator.writeNumberField("total_pages", page.getTotalPages());
    jsonGenerator.writeNumberField("total_elements", page.getTotalElements());
    jsonGenerator.writeNumberField("elements", page.getNumberOfElements());
    jsonGenerator.writeNumberField("page_size", page.getSize());
    jsonGenerator.writeNumberField("page_number", page.getNumber());
    jsonGenerator.writeBooleanField("first_page", page.isFirst());
    jsonGenerator.writeBooleanField("last_page", page.isLast());
    jsonGenerator.writeEndObject();
  }
}
