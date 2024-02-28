package dev.maizy.myna.integration_test;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.redis.testcontainers.RedisContainer;
import dev.maizy.myna.integration_test.container.Postgres;
import dev.maizy.myna.integration_test.container.Redis;
import static org.assertj.core.api.Assertions.assertThat;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"default", "test"})
class GameStateFlowTest {

  private static final Logger log = LoggerFactory.getLogger(GameStateFlowTest.class);

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
  void shouldInitSessionAndPersistUid(@Autowired WebTestClient webClient) {
    final var response1 = webClient
        .get().uri("/whoami")
        .exchange()
        .expectStatus().isOk()
        .expectCookie().exists("myna_sid")
        .expectBody(String.class).returnResult();

    final var sid = getSid(response1);
    final var body1 = response1.getResponseBody();

    final var response2 = webClient
        .get().uri("/whoami").cookie("myna_sid", sid)
        .exchange()
        .expectStatus().isOk()
        .expectCookie().doesNotExist("myna_sid")
        .expectBody(String.class).returnResult();

    assertThat(body1).isEqualTo(response2.getResponseBody());
  }

  private record CreatedGame(String ownerSid, String lobbyLocation) {
  }

  private CreatedGame createGame(WebTestClient webClient) {
    // get create form
    final var createFormResponse = webClient
        .get()
          .uri("/games/create")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).returnResult();
    final var sid = getSid(createFormResponse);
    final var createGameCsrfToken = getCsrfFormToken(createFormResponse);

    // create game
    final var createParams = new LinkedMultiValueMap<String, String>();
    createParams.add("_csrf", createGameCsrfToken);
    createParams.add("rulesetId", "checkers_v0");
    createParams.add("me", "Whites");
    createParams.add("playerName[Whites]", "Me");
    createParams.add("playerName[Blacks]", "Competitor");
    final var createResponse = webClient
        .post()
          .uri("/games/create")
          .cookie("myna_sid", sid)
          .body(BodyInserters.fromFormData(createParams))
        .exchange()
        .expectStatus().isFound()
        .returnResult(Void.class);
    final var lobbyLocation = createResponse.getResponseHeaders().getFirst("Location");
    assertThat(lobbyLocation).isNotNull();
    return new CreatedGame(sid, lobbyLocation);
  }

  @Test
  void shouldAllowOwnerToCreateGameAndChangeItState(@Autowired WebTestClient webClient) {
    final var createdGame = createGame(webClient);
    final var sid = createdGame.ownerSid();
    final var lobbyLocation = createdGame.lobbyLocation();

    // lobby
    log.info("Get lobby: {}", lobbyLocation);
    final var lobbyResponse = webClient
        .get()
          .uri(lobbyLocation)
          .cookie("myna_sid", sid)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).returnResult();
    final var startGameCsrfToken = getCsrfFormToken(lobbyResponse);

    // start game
    log.info("Start game: {}", lobbyLocation);
    final var startParams = new LinkedMultiValueMap<String, String>();
    startParams.add("_csrf", startGameCsrfToken);
    startParams.add("action", "start");
    final var startResponse = webClient
        .post()
          .uri(lobbyLocation)
          .cookie("myna_sid", sid)
          .body(BodyInserters.fromFormData(startParams))
        .exchange()
        .expectStatus().isFound()
        .returnResult(Void.class);
    final var playgroundLocation = startResponse.getResponseHeaders().getFirst("Location");
    assertThat(playgroundLocation).isNotNull();

    // playground
    log.info("Get playground: {}", playgroundLocation);
    final var playgroundResponse = webClient
        .get()
          .uri(playgroundLocation)
          .cookie("myna_sid", sid)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).returnResult();
    final var endGameCsrfToken = getCsrfFormToken(playgroundResponse);

    // end game
    log.info("End game: {}", playgroundLocation);
    final var endParams = new LinkedMultiValueMap<String, String>();
    endParams.add("_csrf", endGameCsrfToken);
    endParams.add("action", "end-game");
    final var endResponse = webClient
        .post()
          .uri(playgroundLocation)
          .cookie("myna_sid", sid)
          .body(BodyInserters.fromFormData(endParams))
        .exchange()
        .expectStatus().isFound()
        .returnResult(Void.class);
    final var creditsLocation = endResponse.getResponseHeaders().getFirst("Location");
    assertThat(creditsLocation).isNotNull();

    // credits
    log.info("Get credits: {}", creditsLocation);
    webClient
        .get()
          .uri(creditsLocation)
          .cookie("myna_sid", sid)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldAllowOtherPlayersJoining(@Autowired WebTestClient webClient) {
    final var createdGame = createGame(webClient);
    final var ownerSid = createdGame.ownerSid();
    final var lobbyLocation = createdGame.lobbyLocation();

    // lobby for owner
    final var ownerLobbyBody = webClient
        .get()
          .uri(lobbyLocation)
          .cookie("myna_sid", ownerSid)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).returnResult().getResponseBody();
    assertThat(ownerLobbyBody).isNotNull();

    final var joinLinks = Jsoup.parse(ownerLobbyBody).getElementsByTag("code");
    assertThat(joinLinks).hasSize(1);

    final var joinLink = joinLinks.get(0).text();

    // player join page
    final var joinResult = webClient
        .get()
          .uri(joinLink)
        .exchange()
        .expectStatus().isOk()
        .expectCookie().exists("myna_sid")
        .expectBody(String.class).returnResult();

    final var playerSid = getSid(joinResult);
    final var joinCsrf = getCsrfFormToken(joinResult);

    // player joins the game
    final var joinResponse = webClient
        .post()
          .uri(joinLink)
          .cookie("myna_sid", playerSid)
          .body(BodyInserters.fromFormData("_csrf", joinCsrf))
        .exchange()
        .expectStatus().isFound()
        .returnResult(Void.class);
    final var playerLobbyLocation = joinResponse.getResponseHeaders().getFirst("Location");
    assertThat(playerLobbyLocation).isNotNull();

    // lobby for player
    webClient
        .get()
          .uri(playerLobbyLocation)
          .cookie("myna_sid", playerSid)
        .exchange()
        .expectStatus().isOk();
  }

  private static String getSid(EntityExchangeResult<String> response) {
    final var cookies = response.getResponseCookies();
    assertThat(cookies).containsKey("myna_sid");
    final var sidCookies = cookies.get("myna_sid");
    assertThat(sidCookies).hasSize(1);
    return sidCookies.get(0).getValue();
  }

  private static String getCsrfFormToken(EntityExchangeResult<String> response) {
    final var body = response.getResponseBody();
    assertThat(body).isNotNull();
    final var doc = Jsoup.parse(body);
    final var forms = doc.getElementsByTag("form");
    assertThat(forms).hasSize(1);
    final var form = forms.get(0);
    final var inputs = form.selectXpath("input[@name='_csrf']");
    assertThat(inputs).hasSize(1);
    final var csrfAttr = inputs.get(0).attr("value");
    assertThat(csrfAttr).isNotNull();
    return csrfAttr;
  }
}
