package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    // TODO: force SNAKE_CASE also for JPA entity's jsons
    // return builder -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    return builder -> builder.build();
  }
}
