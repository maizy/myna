package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.maizy.myna.configuration.JacksonConfiguration;
import dev.maizy.myna.game_message.event.ImmutablePlayerState;
import dev.maizy.myna.game_message.event.ImmutablePlayerWithStatus;
import dev.maizy.myna.game_message.event.ImmutablePlayersState;
import dev.maizy.myna.game_message.event.PlayersState;
import dev.maizy.myna.game_message.request.ImmutableParameterlessRequest;
import dev.maizy.myna.game_message.response.ImmutableWhoYouAre;
import dev.maizy.myna.ruleset.ImmutablePlayer;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@JsonTest
@Import(JacksonConfiguration.class)
@ActiveProfiles({"default", "test"})
class MessageJsonTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private JacksonTester<Message> jacksonTester;

  @Test
  public void testWhoAmIRequest() throws IOException {
    final var whoAmI = ImmutableParameterlessRequest.builder().requestType(RequestType.who_am_i).gameId("test").build();
    roundTrip(whoAmI);
    roundTrip(whoAmI.withOid("123"));
  }

  @Test
  public void testWhoYouAreResponse() throws IOException {
    final var whoYouAre = ImmutableWhoYouAre.builder().gameId("test").player(ImmutablePlayer.of("p1")).build();
    roundTrip(whoYouAre);
    roundTrip(whoYouAre.withOid("abc"));
  }

  @Test
  public void testPlayerStateEvent() throws IOException {
    roundTrip(
        ImmutablePlayerState.builder()
            .gameId("test").eventType(EventType.player_connected).player(ImmutablePlayer.of("p1"))
            .build()
    );
  }

  @Test
  public void testPlayersStateEvent() throws IOException {
    roundTrip(
        ImmutablePlayersState.builder()
            .gameId("test")
            .addPlayer(ImmutablePlayerWithStatus.of(
                ImmutablePlayer.builder().roleName("Player 1").id("p1").build(), PlayersState.PlayerStatus.played
            ))
            .addPlayer(ImmutablePlayerWithStatus.of(
                ImmutablePlayer.of("p2"), PlayersState.PlayerStatus.absent
            ))
            .build()
    );
  }

  @Test
  public void testMissedKind() {
    assertDeserializationThrows(
      """
      {"gameId": "test", "requestType": "who_am_i"}
      """,
      JsonProcessingException.class
    );
  }

  @Test
  public void testUnknownKind() {
    assertDeserializationThrows(
      """
      {"gameId": "test", "requestType": "who_am_i", "kind": "broadcast"}
      """,
      JsonProcessingException.class
    );
  }

  @Test
  public void testMissedType() {
    assertDeserializationThrows(
      """
      {"gameId": "test", "kind": "request"}
      """,
      JsonProcessingException.class
    );
  }

  @Test
  public void testUnknownType() {
    assertDeserializationThrows(
      """
      {"gameId":"test", "requestType":"mmm", "kind":"request"}
      """,
      JsonProcessingException.class
    );
  }

  private void assertDeserializationThrows(String json, Class<? extends Throwable> klass) {
    assertThrows(klass, () -> jacksonTester.parse(json));
  }

  private void roundTrip(Message message) throws IOException {
    final var serialized = jacksonTester.write(message);
    final var json = serialized.getJson();
    logger.debug("Message JSON for class {}:\n{}", message.getClass().getSimpleName(), json);
    final var deserializedMessage = jacksonTester.parse(json).getObject();
    logger.debug("Deserialized message: {}", deserializedMessage);
    assertEquals(message, deserializedMessage);
  }

}
