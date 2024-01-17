package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.http.helper.GameRedirectHelper;
import dev.maizy.myna.service.GameStateService;
import dev.maizy.myna.service.GameStateServiceErrors;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/game")
public class GameController {

  final private GameStateService gameStateService;

  public GameController(GameStateService gameStateService) {
    this.gameStateService = gameStateService;
  }

  @GetMapping("{gameId}/lobby")
  @ResponseBody
  public String lobby(@PathVariable String gameId) {
    return "game " + gameId;
  }

  @GetMapping("{gameId}/join/{joinKey}")
  public ModelAndView join(@PathVariable String gameId, @PathVariable String joinKey) {
    final var view = new ModelAndView("game/join");
    final var maybeGameAndPlayer = checkJoinKeyOrDisplayError(view, gameId, joinKey);
    maybeGameAndPlayer.ifPresent(gameAndPlayer -> {
      final var game = gameAndPlayer.game();
      final var ruleset = game.getRuleset().getRuleset();
      final var player = gameAndPlayer.player();
      final var maybeRulesetPlayer = ruleset.getPlayerById(player.getId().getRulesetPlayerId());
      maybeRulesetPlayer.ifPresent(rulesetPlayer -> view.addObject("roleName", rulesetPlayer.roleName()));
      view.addObject("name", player.getName());
      view.addObject("rulesetName", ruleset.name());
      view.addObject("joinAllowed", true);
    });
    return view;
  }

  @PostMapping("{gameId}/join/{joinKey}")
  public ModelAndView join(@PathVariable String gameId, @PathVariable String joinKey, Authentication auth) {
    final var view = new ModelAndView("game/join");
    final var maybeGameAndPlayer = checkJoinKeyOrDisplayError(view, gameId, joinKey);
    maybeGameAndPlayer.ifPresent(gameAndPlayer -> {
      gameStateService.joinGame(gameAndPlayer, (String) auth.getPrincipal());
      view.setViewName(GameRedirectHelper.redirectBasedOnGameState(gameAndPlayer.game()));
    });
    return view;
  }

  private Optional<GameStateService.GameAndPlayer> checkJoinKeyOrDisplayError(
      ModelAndView view, String gameId, String joinKey) {
    final Optional<GameStateService.GameAndPlayer> maybeGameAndPlayer;
    try {
      maybeGameAndPlayer = gameStateService.checkJoinKey(gameId, joinKey);
    } catch (GameStateServiceErrors.GameNotFound e) {
      view.addObject("error", e.getMessage());
      view.setStatus(HttpStatus.NOT_FOUND);
      return Optional.empty();
    } catch (GameStateServiceErrors.ActionIsForbiddenInCurrentGameState e) {
      view.addObject("error", e.getMessage());
      view.setStatus(HttpStatus.FORBIDDEN);
      return Optional.empty();
    }
    if (maybeGameAndPlayer.isEmpty()) {
      view.addObject("error", "You aren't allowed to join");
      view.setStatus(HttpStatus.FORBIDDEN);
    }
    return maybeGameAndPlayer;
  }
}
