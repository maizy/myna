package dev.maizy.myna.service.game_events;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

public interface ConnectedPlayerDispatcher {
  void broadcastMessageToConnectedPlayers(String gameId, String body);
}
