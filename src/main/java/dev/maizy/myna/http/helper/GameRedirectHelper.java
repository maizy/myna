package dev.maizy.myna.http.helper;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.GameEntity;

public class GameRedirectHelper {

  static public String redirectBasedOnGameState(GameEntity game) {
    return switch (game.getState()) {
      case created, upcomming -> "redirect:/game/" + game.getId() + "/lobby";
      // TODO
      default -> throw new RuntimeException("unsupported game state");
    };
  }
}
