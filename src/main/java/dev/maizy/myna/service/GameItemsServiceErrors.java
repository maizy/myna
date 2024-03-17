package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

public class GameItemsServiceErrors {
  public static class GameItemsServiceException extends RuntimeException {
    private final String gameId;

    public GameItemsServiceException(String gameId, String message) {
      super(message);
      this.gameId = gameId;
    }

    public GameItemsServiceException(String gameId, String message, Throwable cause) {
      super(message, cause);
      this.gameId = gameId;
    }
  }

  public static class Forbidden extends GameItemsServiceException {
    public Forbidden(String gameId, String message) {
      super(gameId, message);
    }

    public Forbidden(String gameId, String message, Throwable cause) {
      super(gameId, message, cause);
    }
  }
}
