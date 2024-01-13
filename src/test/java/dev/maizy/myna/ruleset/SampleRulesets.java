package dev.maizy.myna.ruleset;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.surface.Colors;
import dev.maizy.myna.surface.ImmutablePoint;
import dev.maizy.myna.surface.ImmutableRectangle;
import dev.maizy.myna.surface.ImmutableRectangleAppearance;
import dev.maizy.myna.surface.ImmutableSize;

public class SampleRulesets {
  static public Ruleset allFeaturesRuleset() {
    final ObjectsGroup rootObjects = ImmutableObjectsGroup.builder()
        .addObject(
            ImmutableGameObject.builder()
                .id("dice")
                .size(ImmutableSize.of(20, 20))
                .addState(
                    ImmutableObjectState.builder()
                        .id("1")
                        .appearance(ImmutableRectangleAppearance.builder().text("⚀").build())
                        .build()
                )
                .addState(
                    ImmutableObjectState.builder()
                        .id("2")
                        .appearance(ImmutableRectangleAppearance.builder().text("⚁").build())
                        .build()
                )
                .addState(
                    ImmutableObjectState.builder()
                        .id("3")
                        .appearance(ImmutableRectangleAppearance.builder().text("⚂").build())
                        .build()
                )
                .addState(
                    ImmutableObjectState.builder()
                        .id("4")
                        .appearance(ImmutableRectangleAppearance.builder().text("⚃").build())
                        .build()
                )
                .addState(
                    ImmutableObjectState.builder()
                        .id("5")
                        .appearance(ImmutableRectangleAppearance.builder().text("⚄").build())
                        .build()
                )
                .addState(
                    ImmutableObjectState.builder()
                        .id("6")
                        .appearance(ImmutableRectangleAppearance.builder().text("⚅").build())
                        .build()
                )
                .stateChangeStrategy(ObjectStateChangeStrategy.random)
                .build()
        )
        .addObject(
            ImmutableGameObject.builder()
                .id("black mark")
                .size(ImmutableSize.of(10, 10))
                .addState(
                    ImmutableObjectState.builder()
                        .appearance(ImmutableRectangleAppearance.builder().text("*").build())
                        .build()
                )
                .build()
        )
        .build();

    final var master = ImmutablePlayer.builder().id("master").roleName("Master").build();
    final var p1 = ImmutablePlayer.builder().id("p1").roleName("Player 1").build();
    final var p2 = ImmutablePlayer.builder().id("p2").roleName("Player 2").build();
    final var p3 = ImmutablePlayer.builder().id("p3").roleName("Player 3").build();

    final var allowForAllAccess = ImmutableStaticAccess.builder()
        .visibleForAll(true)
        .ownedForAll(true)
        .build();

    final var gameZone = ImmutableZone.builder()
        .access(allowForAllAccess)
        .position(
            ImmutableRectangle.of(
                ImmutablePoint.of(0, 0),
                ImmutablePoint.of(1600, 1024)
            )
        )
        .appearance(
            ImmutableRectangleAppearance.builder()
                .backgroundColor(Colors.yellow.hex)
                .build()
        )
        .build();

    final var allowForP1Access = ImmutableStaticAccess.builder()
        .visibleForAll(false)
        .ownedForAll(false)
        .addOwner(p1.ref())
        .addViewer(master.ref())
        .build();

    final var p1Zone = ImmutableZone.builder()
        .access(allowForP1Access)
        .position(
            ImmutableRectangle.of(
                ImmutablePoint.of(100, 1200),
                ImmutablePoint.of(500, 1300)
            )
        )
        .build();

    final var allowForP2Access = ImmutableStaticAccess.builder()
        .visibleForAll(false)
        .ownedForAll(false)
        .addOwner(p2.ref())
        .addViewer(master.ref())
        .build();

    final var p2Zone = ImmutableZone.builder()
        .access(allowForP2Access)
        .position(
            ImmutableRectangle.of(
                ImmutablePoint.of(600, 1200),
                ImmutablePoint.of(1000, 1300)
            )
        )
        .build();

    final var allowForP3Access = ImmutableStaticAccess.builder()
        .visibleForAll(false)
        .ownedForAll(false)
        .addOwner(p3.ref())
        .addViewer(master.ref())
        .build();

    final var p3Zone = ImmutableZone.builder()
        .access(allowForP3Access)
        .position(
            ImmutableRectangle.of(
                ImmutablePoint.of(1100, 1200),
                ImmutablePoint.of(1500, 1300)
            )
        )
        .build();

    final var extrasStack = ImmutableObjectsGroup.builder()
        .id("extras")
        .addObject(
            ImmutableGameObject.builder()
                .size(ImmutableSize.of(30, 10))
                .addState(
                    ImmutableObjectState.builder()
                        .appearance(ImmutableRectangleAppearance.builder().text("Do it again").build())
                        .build()
                )
                .build()
        )
        .addObject(
            ImmutableGameObject.builder()
                .size(ImmutableSize.of(30, 10))
                .addState(
                    ImmutableObjectState.builder()
                        .appearance(ImmutableRectangleAppearance.builder().text("Two extra steps").build())
                        .build()
                )
                .build()
        )
        .build();

    final var penaltiesStack = ImmutableObjectsGroup.builder()
        .id("penalties")
        .addObject(
            ImmutableGameObject.builder()
                .size(ImmutableSize.of(30, 10))
                .addState(
                    ImmutableObjectState.builder()
                        .appearance(ImmutableRectangleAppearance.builder().text("Skip next turn").build())
                        .build()
                )
                .build()
        )
        .addObject(
            ImmutableGameObject.builder()
                .size(ImmutableSize.of(30, 10))
                .addState(
                    ImmutableObjectState.builder()
                        .appearance(ImmutableRectangleAppearance.builder().text("Go two steps back").build())
                        .build()
                )
                .build()
        )
        .build();

    return ImmutableRuleset
        .builder()
        .id("all_features")
        .name("All Features Ruleset")
        .description("""
            The ruleset consists of all possible objects are available in the Myna engine.
            Some of objects are not supported yet."""
        )
        .gameZone(gameZone)
        .addPlayers(master, p1, p2, p3)
        .addZones(p1Zone, p2Zone, p3Zone)
        .rootObjects(rootObjects)
        .addObjectsStack(extrasStack)
        .addObjectsStack(penaltiesStack)
        .build();
  }

  static public Ruleset checkersRuleset() {
    // TODO: position all checkers on the board
    final var black = ImmutableObjectState.builder()
        .id("black")
        .appearance(ImmutableRectangleAppearance.builder().text("⚫️").build())
        .build();

    final var white = ImmutableObjectState.builder()
        .id("white")
        .appearance(ImmutableRectangleAppearance.builder().text("⚪️").build())
        .build();

    final var rootObjectsBuilder = ImmutableObjectsGroup.builder();

    for (var i = 0; i < 12; i++) {
      rootObjectsBuilder.addObject(
          ImmutableGameObject.builder()
              .id("b" + i)
              .size(ImmutableSize.of(20, 20))
              .addState(black)
              .build()
      );
    }

    for (var i = 0; i < 12; i++) {
      rootObjectsBuilder.addObject(
          ImmutableGameObject.builder()
              .id("w" + i)
              .size(ImmutableSize.of(20, 20))
              .addState(white)
              .build()
      );
    }

    final var whites = ImmutablePlayer.of("Whites");
    final var blacks = ImmutablePlayer.of("Blacks");

    final var gameZone = ImmutableZone.builder()
        .position(
            ImmutableRectangle.of(
                ImmutablePoint.of(0, 0),
                ImmutablePoint.of(1024, 1024)
            )
        )
        .appearance(
            ImmutableRectangleAppearance.builder()
                .backgroundColor(Colors.yellow.hex)
                .build()
        )
        .build();

    return ImmutableRuleset
        .builder()
        .id("checkers")
        .name("Checkers")
        .description("""
            Checkers, also known as draughts, is a strategy board game for two players which involve forward \
            movements of uniform game pieces and mandatory captures by jumping over opponent pieces.
            Checkers is played by two opponents on opposite sides of the game board. One player has black pieces \
            the other has white pieces. White moves first, then players alternate turns. A player cannot move \
            the opponent's pieces. A move consists of moving a piece forward to an adjacent unoccupied square. \
            If the adjacent square contains an opponent's piece, and the square immediately beyond it is vacant, \
            the piece may be captured (and removed from the game) by jumping over it."""
        )
        .gameZone(gameZone)
        .addPlayers(whites, blacks)
        .rootObjects(rootObjectsBuilder.build())
        .build();
  }
}
