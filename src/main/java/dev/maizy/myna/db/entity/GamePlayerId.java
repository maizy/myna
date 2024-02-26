package dev.maizy.myna.db.entity;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GamePlayerId implements Serializable {

  public GamePlayerId() {
  }

  public GamePlayerId(String gameId, String rulesetPlayerId) {
    this.gameId = gameId;
    this.rulesetPlayerId = rulesetPlayerId;
  }

  @Column(nullable = false)
  private String gameId;

  @Column(nullable = false)
  private String rulesetPlayerId;

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public String getRulesetPlayerId() {
    return rulesetPlayerId;
  }

  public void setRulesetPlayerId(String rulesetPlayerId) {
    this.rulesetPlayerId = rulesetPlayerId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GamePlayerId that = (GamePlayerId) o;
    return gameId.equals(that.gameId) && rulesetPlayerId.equals(that.rulesetPlayerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameId, rulesetPlayerId);
  }
}
