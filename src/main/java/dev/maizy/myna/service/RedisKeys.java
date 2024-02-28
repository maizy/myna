package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.util.List;

/**
 * All used redis keys
 */
public final class RedisKeys {

  /**
   * pubsub
   */
  public static String getMessagesKey(String gameId) {
    return "game-messages:" + gameId;
  }

  /**
   * hash
   * fields – rulesetRoleId
   * value – uid when a player is connected
   */
  public static String playersState(String gameId) {
    return "players-status:" + gameId;
  }

  public static List<String> getAllGameKeys(String gameId) {
    return List.of(
        playersState(gameId)
    );
  }


  private RedisKeys() {
  }
}
