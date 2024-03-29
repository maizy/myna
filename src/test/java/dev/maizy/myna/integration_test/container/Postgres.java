package dev.maizy.myna.integration_test.container;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public final class Postgres {

  public static final String version = "16.1-alpine";

  public static PostgreSQLContainer<?> build() {
    return new PostgreSQLContainer<>("postgres:" + version);
  }

  public static void use(PostgreSQLContainer<?> instance, DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", instance::getJdbcUrl);
    registry.add("spring.datasource.username", instance::getUsername);
    registry.add("spring.datasource.password", instance::getPassword);
  }

  private Postgres() {
  }
}
