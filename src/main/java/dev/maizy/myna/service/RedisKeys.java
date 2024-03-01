package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.util.Arrays;
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

  /**
   * hash
   * fields – itemId
   * value – JSON with item state
   */
  public static String itemsState(String gameId) {
    return "items:" + gameId;
  }

  public static List<String> getAllGameKeys(String gameId) {
    return Arrays.asList(
        playersState(gameId),
        itemsState(gameId)
    );
  }


  private RedisKeys() {
  }
}
