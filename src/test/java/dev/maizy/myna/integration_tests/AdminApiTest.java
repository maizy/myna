package dev.maizy.myna.integration_tests;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"default", "test"})
class AdminApiTest {

  @Container
  private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.1-alpine");

  private static final String redisPass = "testpass";

  @Container
  private static final RedisContainer redisContainer =
      new RedisContainer(DockerImageName.parse("redis:7.2.3-alpine"))
          .withExposedPorts(6379)
          .withCommand("redis-server", "--requirepass", redisPass);

  @BeforeAll
  static void beforeAll() {
    postgreSQLContainer.start();
    redisContainer.start();
  }

  @AfterAll
  static void afterAll() {
    postgreSQLContainer.stop();
    redisContainer.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

    registry.add("spring.redis.host", redisContainer::getHost);
    registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    registry.add("spring.redis.password", () -> redisPass);
  }


  @Test
  void shouldForbidAccessWithoutToken(@Autowired WebTestClient webClient) {
    webClient
        .get().uri("/admin/api/v1/ruleset")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void shouldForbidAccessWithUnknownToken(@Autowired WebTestClient webClient) {
    webClient
        .get()
          .uri("/admin/api/v1/ruleset")
          .header("Authorization", "Bearer unknowntoken")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void shouldAllowAccessWithAnyAdminToken(@Autowired WebTestClient webClient) {
    webClient
        .get()
          .uri("/admin/api/v1/ruleset")
          .header("Authorization", "Bearer admintoken1")
        .exchange()
        .expectStatus().isOk();

    webClient
        .get()
          .uri("/admin/api/v1/ruleset")
          .header("Authorization", "Bearer admintoken2")
        .exchange()
        .expectStatus().isOk();
  }
}
