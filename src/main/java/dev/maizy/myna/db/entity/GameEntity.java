package dev.maizy.myna.db.entity;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.game_state.GameState;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "games")
@TypeDef(name = "game_state_enum", typeClass = PostgreSQLEnumType.class)
public class GameEntity {

  @Id
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ruleset_id", insertable = false, updatable = false)
  private RulesetEntity ruleset;

  @Column(name = "ruleset_id")
  private String rulesetId;

  @Enumerated(EnumType.STRING)
  @Type(type = "game_state_enum")
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
