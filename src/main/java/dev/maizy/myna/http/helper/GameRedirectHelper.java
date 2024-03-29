package dev.maizy.myna.http.helper;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.GameEntity;
import dev.maizy.myna.service.GameStateService;
import java.nio.charset.StandardCharsets;
import org.springframework.web.util.UriUtils;

public final class GameRedirectHelper {

  public static String redirectBasedOnGameState(GameEntity game) {
    return switch (game.getState()) {
      case created, upcomming ->
          "redirect:/game/" + encode(game.getId()) + "/lobby";
      case launched ->
          "redirect:/game/" + encode(game.getId()) + "/playground";
      case finished ->
          "redirect:/game/" + encode(game.getId()) + "/credits";
    };
  }

  public static String redirectBasedOnGameState(GameStateService.GameAccessAuth gameAccessAuth) {
    final var baseRedirect = redirectBasedOnGameState(gameAccessAuth.game());
    if (gameAccessAuth.player() != null) {
      return baseRedirect + "/" + encode(gameAccessAuth.player().getId().getRulesetPlayerId());
    }
    return baseRedirect;
  }

  private static String encode(String pathSegment) {
    return UriUtils.encodePathSegment(pathSegment, StandardCharsets.UTF_8);
  }

  private GameRedirectHelper() {
  }
}
