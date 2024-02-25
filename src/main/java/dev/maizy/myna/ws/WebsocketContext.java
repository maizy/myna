package dev.maizy.myna.ws;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.ruleset.Player;
import dev.maizy.myna.service.GameStateService;
import java.util.Optional;

public class WebsocketContext {

  private final String uid;

  // player & game state aren't synced from DB after connection has been established
  private final GameStateService.GameAccessAuth accessAuthOnConnect;

  public WebsocketContext(String uid, GameStateService.GameAccessAuth accessAuthOnConnect) {
    this.uid = uid;
    this.accessAuthOnConnect = accessAuthOnConnect;
  }

  public String getUid() {
    return uid;
  }

  public Optional<String> getRulesetPlayerId() {
    return accessAuthOnConnect.getRulesetPlayer().map(Player::id);
  }

  public Optional<Player> getRulesetPlayer() {
    return accessAuthOnConnect.getRulesetPlayer();
  }

  public String getGameId() {
    return accessAuthOnConnect.game().getId();
  }
}
