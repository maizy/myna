package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

public enum EventType {
  player_connected,
  player_disconnected,
  players_state_changed,
  game_state_changed,
  object_drag_start,
  object_drag,
  object_drag_end,
  object_move
}
