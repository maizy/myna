package dev.maizy.myna.http.form;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.util.Map;

public class CreateGameForm {
  private String me;
  private String rulesetId;
  private Map<String, String> playerName;

  public String getMe() {
    return me;
  }

  public void setMe(String me) {
    this.me = me;
  }

  public String getRulesetId() {
    return rulesetId;
  }

  public void setRulesetId(String rulesetId) {
    this.rulesetId = rulesetId;
  }

  public Map<String, String> getPlayerName() {
    return playerName;
  }

  public void setPlayerName(Map<String, String> playerName) {
    this.playerName = playerName;
  }

  @Override
  public String toString() {
    return "CreateGameForm{" +
        "me='" + me + '\'' +
        ", rulesetId='" + rulesetId + '\'' +
        ", playerName=" + playerName +
        '}';
  }
}
