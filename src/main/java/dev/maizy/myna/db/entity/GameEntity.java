package dev.maizy.myna.db.entity;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.game_state.GameState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;

@Entity
@Table(name = "games")
public class GameEntity {

  @Id
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ruleset_id", insertable = false, updatable = false)
  private RulesetEntity ruleset;

  @Column(name = "ruleset_id")
  private String rulesetId;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private GameState state;

  private ZonedDateTime createdAt;

  private ZonedDateTime finishedAt;

  private String ownerUid;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public RulesetEntity getRuleset() {
    return ruleset;
  }

  public void setRuleset(RulesetEntity ruleset) {
    this.ruleset = ruleset;
  }

  public String getRulesetId() {
    return rulesetId;
  }

  public void setRulesetId(String rulesetId) {
    this.rulesetId = rulesetId;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ZonedDateTime getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(ZonedDateTime finishedAt) {
    this.finishedAt = finishedAt;
  }

  public String getOwnerUid() {
    return ownerUid;
  }

  public boolean isOwner(String uid) {
    return uid.equals(this.getOwnerUid());
  }

  public void setOwnerUid(String ownerUid) {
    this.ownerUid = ownerUid;
  }

  public GameState getState() {
    return state;
  }

  public void setState(GameState state) {
    this.state = state;
  }
}
