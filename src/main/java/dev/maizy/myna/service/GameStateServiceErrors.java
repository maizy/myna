package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.GameEntity;
import dev.maizy.myna.game_state.GameState;
import java.util.List;

public class GameStateServiceErrors {
  public static class GameStateServiceException extends RuntimeException {
    public GameStateServiceException() {
      super();
    }

    public GameStateServiceException(String message) {
      super(message);
    }

    public GameStateServiceException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class GameNotFound extends GameStateServiceException {
    private final String gameId;

    public GameNotFound(String gameId) {
      super("Game not found");
      this.gameId = gameId;
    }

    public String getGameId() {
      return gameId;
    }
  }


  public static class Forbidden extends GameStateServiceException {
    private final GameEntity game;

    public Forbidden(GameEntity game) {
      this.game = game;
    }

    public Forbidden(String message, GameEntity game) {
      super(message);
      this.game = game;
    }

    public GameEntity getGame() {
      return game;
    }
  }

  public static class NotValidJoinKey extends Forbidden {
    private final String joinKey;

    public NotValidJoinKey(GameEntity game, String joinKey) {
      super(game);
      this.joinKey = joinKey;
    }

    public String getJoinKey() {
      return joinKey;
    }
  }

  public static class ForbiddenInCurrentGameState extends Forbidden {
    private final List<GameState> allowedStates;

    public ForbiddenInCurrentGameState(GameEntity game, List<GameState> allowedStates) {
      super(game);
      this.allowedStates = allowedStates;
    }

    public ForbiddenInCurrentGameState(String message, GameEntity game, List<GameState> allowedStates) {
      super(message, game);
      this.allowedStates = allowedStates;
    }

    public List<GameState> getAllowedStates() {
      return allowedStates;
    }
  }

  public static class GameStateChangeForbidden extends Forbidden {
    private final List<GameState> allowedCurrentStates;
    private final GameState toState;

    public GameStateChangeForbidden(GameEntity game, List<GameState> allowedCurrentStates, GameState toState) {
      super(game);
      this.allowedCurrentStates = allowedCurrentStates;
      this.toState = toState;
    }

    public List<GameState> getAllowedCurrentStates() {
      return allowedCurrentStates;
    }

    public GameState getToState() {
      return toState;
    }
  }
}
