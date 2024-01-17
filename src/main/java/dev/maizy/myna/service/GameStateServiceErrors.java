package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.game_state.GameState;
import java.util.List;

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

  public static class GameNotFound extends GameStateServiceException {
    final private String gameId;

    public GameNotFound(String gameId) {
      super("Game not found");
      this.gameId = gameId;
    }

    public GameNotFound(String message, String gameId) {
      super(message);
      this.gameId = gameId;
    }

    public GameNotFound(String message, Throwable cause, String gameId) {
      super(message, cause);
      this.gameId = gameId;
    }
  }

  public static class ActionIsForbiddenInCurrentGameState extends GameStateServiceException {
    final private String gameId;
    final private GameState currentState;
    final private List<GameState> actionAllowedStates;

    public ActionIsForbiddenInCurrentGameState(
        String message, String gameId, GameState currentState, List<GameState> actionAllowedStates) {
      super(message);
      this.gameId = gameId;
      this.currentState = currentState;
      this.actionAllowedStates = actionAllowedStates;
    }

    public ActionIsForbiddenInCurrentGameState(
        String message, Throwable cause, String gameId, GameState currentState, List<GameState> actionAllowedStates) {
      super(message, cause);
      this.gameId = gameId;
      this.currentState = currentState;
      this.actionAllowedStates = actionAllowedStates;
    }
  }
}
