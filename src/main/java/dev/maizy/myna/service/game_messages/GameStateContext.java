package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.GameEntity;

public record GameStateContext(GameEntity game) {
  public String gameId() {
    return game.getId();
  }
}
