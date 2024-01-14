package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.game_state.GameState;

public class GameStateServiceErrors {
  public static class GameStateServiceException extends RuntimeException {
    public GameStateServiceException(String message) {
      super(message);
    }

    public GameStateServiceException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class GameStateChangeError extends GameStateServiceException {
    final private GameState fromState;
    final private GameState toState;

    public GameStateChangeError(GameState fromState, GameState toState, String message) {
      super(message);
      this.fromState = fromState;
      this.toState = toState;
    }

    public GameStateChangeError(GameState fromState, GameState toState, String message, Throwable cause) {
      super(message, cause);
      this.fromState = fromState;
      this.toState = toState;
    }

    public GameState getFromState() {
      return fromState;
    }

    public GameState getToState() {
      return toState;
    }
  }
}
