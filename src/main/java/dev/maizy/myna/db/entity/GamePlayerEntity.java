package dev.maizy.myna.db.entity;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "game_players")
public class GamePlayerEntity {

  @Id
  private GamePlayerId id;

  private String name;

  @Nullable
  private String uid;

  private String joinKey;

  @Nullable
  private ZonedDateTime joinedAt;

  @Column(nullable = false)
  private int rulesetOrder;

  public GamePlayerId getId() {
    return id;
  }

  public void setId(GamePlayerId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Nullable
  public String getUid() {
    return uid;
  }

  public void setUid(@Nullable String uid) {
    this.uid = uid;
  }

  public String getJoinKey() {
    return joinKey;
  }

  public void setJoinKey(String joinKey) {
    this.joinKey = joinKey;
  }

  public int getRulesetOrder() {
    return rulesetOrder;
  }

  public void setRulesetOrder(int rulesetOrder) {
    this.rulesetOrder = rulesetOrder;
  }

  @Nullable
  public ZonedDateTime getJoinedAt() {
    return joinedAt;
  }

  public void setJoinedAt(@Nullable ZonedDateTime joinedAt) {
    this.joinedAt = joinedAt;
  }
}
