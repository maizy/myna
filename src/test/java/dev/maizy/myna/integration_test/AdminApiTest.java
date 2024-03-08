package dev.maizy.myna.integration_test;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import dev.maizy.myna.integration_test.container.Postgres;
import dev.maizy.myna.integration_test.container.Redis;
import dev.maizy.myna.ruleset.SampleRulesets;
import dev.maizy.myna.service.GameStateService;
import java.util.Collections;
import java.util.Optional;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"default", "test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminApiTest {

  public static final String testRulesetId = "checkers_v1";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private GameStateService gameStateService;

  @Container
  private static final PostgreSQLContainer<?> postgreSQLContainer = Postgres.build();

  @Container
  private static final RedisContainer redisContainer = Redis.build();

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
    Postgres.use(postgreSQLContainer, registry);
    Redis.use(redisContainer, registry);
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

  @Test
  @Order(1)
  void shouldAddRuleset(@Autowired WebTestClient webClient) throws Exception {
    final var sampleRuleset = SampleRulesets.allFeaturesRuleset();
    final var rulesetJson = objectMapper.writeValueAsString(sampleRuleset);

    webClient
        .get()
          .uri("/admin/api/v1/ruleset/{id}", sampleRuleset.id())
          .accept(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer admintoken1")
        .exchange()
        .expectStatus().isNotFound()
            .expectBody().json("""
              {"error": "not_found"}
            """);

    webClient
        .post()
          .uri("/admin/api/v1/ruleset")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer admintoken1")
          .bodyValue(rulesetJson)
        .exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$.id").isEqualTo(sampleRuleset.id());

    webClient
        .get()
          .uri("/admin/api/v1/ruleset/{id}", sampleRuleset.id())
          .header("Authorization", "Bearer admintoken1")
          .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody().json(rulesetJson);
  }

  @Test
  @Order(2)
  void shouldReturnRulesetsList(@Autowired WebTestClient webClient) {
    webClient
        .get()
          .uri("/admin/api/v1/ruleset")
          .accept(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer admintoken1")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
          // 2 predefined rulesets & one added above
          .jsonPath("$.total_elements").isEqualTo(3)
          .jsonPath("$.content").value(hasSize(3));
  }

  @Test
  @Order(3)
  void shouldDeleteRulesetWithoutPlayedGames(@Autowired WebTestClient webClient) {
    final var sampleRuleset = SampleRulesets.allFeaturesRuleset();

    webClient
        .delete()
          .uri("/admin/api/v1/ruleset/{id}", sampleRuleset.id())
          .header("Authorization", "Bearer admintoken1")
        .exchange()
        .expectStatus().isNoContent();

    webClient
        .get()
          .uri("/admin/api/v1/ruleset/{id}", sampleRuleset.id())
          .header("Authorization", "Bearer admintoken1")
          .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  @Order(4)
  void shouldNotDeleteRulesetWithPlayedGames(@Autowired WebTestClient webClient) {

    gameStateService.createGame(testRulesetId, "owner", Collections.emptyMap(), Optional.empty());

    webClient
        .delete()
          .uri("/admin/api/v1/ruleset/{id}", testRulesetId)
          .header("Authorization", "Bearer admintoken1")
        .exchange()
        .expectStatus().isForbidden()
        .expectBody().jsonPath("error").isEqualTo("unable");
  }
}
