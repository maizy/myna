package dev.maizy.myna.game_model;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.configuration.JacksonConfiguration;
import dev.maizy.myna.surface.Colors;
import dev.maizy.myna.surface.ImmutablePoint;
import dev.maizy.myna.surface.ImmutableRectangle;
import dev.maizy.myna.surface.ImmutableRectangleAppearance;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
class GameModelJsonTest {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private JacksonTester<GameModel> jacksonTester;

  @Test
  public void testModelWithOnlyGameZoneAndObjects() throws IOException {
    final var gameZone = ImmutableZone.builder()
        .itemId("root")
        .appereance(ImmutableRectangleAppearance.builder().backgroundColor(Colors.white.hex()).build())
        .build();

    final var gameZoneRef = Ref.fromItem(gameZone);
    final var object0 = ImmutableGameObject.builder()
        .itemId("obj-0")
        .container(gameZoneRef)
        .currentPosition(ImmutableRectangle.of(ImmutablePoint.of(50, 10), ImmutablePoint.of(150, 210)))
        .currentAppereance(ImmutableRectangleAppearance.builder().text("Object 1").build())
        .build();

    final var object1 = ImmutableGameObject.builder()
        .itemId("obj-1")
        .container(gameZoneRef)
        .currentPosition(ImmutableRectangle.of(ImmutablePoint.of(0, 0), ImmutablePoint.of(10, 10)))
        .currentAppereance(ImmutableRectangleAppearance.builder().backgroundColor(Colors.black.hex()).build())
        .isDraggable(true)
        .build();

    final var model = ImmutableGameModel.builder()
        .gameZone(gameZone)
        .addObject(object0)
        .addObject(object1)
        .build();

    roundTrip(model);
  }

  private void roundTrip(GameModel model) throws IOException {
    final var serialized = jacksonTester.write(model);
    final var json = serialized.getJson();
    logger.debug("Game model JSON:\n{}", json);
    final var deserialized = jacksonTester.parse(json).getObject();
    logger.debug("Deserialized game model:\n{}", deserialized);
    assertEquals(model, deserialized);
  }
}
