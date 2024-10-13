package dev.maizy.myna.db.entity;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.ruleset.Ruleset;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "rulesets")
public class RulesetEntity {

  @Id
  private String id;

  @JdbcTypeCode(SqlTypes.JSON)
  private Ruleset ruleset;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public dev.maizy.myna.ruleset.Ruleset getRuleset() {
    return ruleset;
  }

  public void setRuleset(dev.maizy.myna.ruleset.Ruleset ruleset) {
    this.ruleset = ruleset;
  }
}
