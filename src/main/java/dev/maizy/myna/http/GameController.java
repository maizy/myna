package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.dto.html.ImmutableLobbyPlayer;
import dev.maizy.myna.http.helper.GameRedirectHelper;
import dev.maizy.myna.service.GameStateService;
import dev.maizy.myna.service.GameStateServiceErrors;
import dev.maizy.myna.service.UriService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/game")
public class GameController {

  private final GameStateService gameStateService;
  private final UriService uriService;

  public GameController(GameStateService gameStateService, UriService uriService) {
    this.gameStateService = gameStateService;
    this.uriService = uriService;
  }

  @GetMapping("")
  public String index() {
    return "redirect:/games/create";
  }

  @GetMapping("{gameId}")
  public String gameIndex(@PathVariable String gameId, Authentication auth) {
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUid(gameId, (String) auth.getPrincipal());
    return GameRedirectHelper.redirectBasedOnGameState(gameAccessAuth);
  }

  @GetMapping("{gameId}/join/{joinKey}")
  public ModelAndView join(@PathVariable String gameId, @PathVariable String joinKey) {
    final var gameAccessAuth = gameStateService.checkJoinGame(gameId, joinKey);
    final var view = new ModelAndView("game/join");
    final var game = gameAccessAuth.game();
    final var ruleset = game.getRuleset().getRuleset();
    final var player = gameAccessAuth.player();
    final var maybeRulesetPlayer = gameAccessAuth.getRulesetPlayer();
    maybeRulesetPlayer.ifPresent(rulesetPlayer -> view.addObject("roleName", rulesetPlayer.roleName()));
    if (player != null) {
      view.addObject("name", player.getName());
    }
    view.addObject("rulesetName", ruleset.name());
    return view;
  }

  @PostMapping("{gameId}/join/{joinKey}")
  public String join(@PathVariable String gameId, @PathVariable String joinKey, Authentication auth) {
    final var gameAccessAuth = gameStateService.checkJoinGame(gameId, joinKey);
    gameStateService.joinGame(gameAccessAuth, (String) auth.getPrincipal());
    return GameRedirectHelper.redirectBasedOnGameState(gameAccessAuth);
  }

  /**
   * Use first found role by uid if any
   * One uid could play for several roles
   */
  @GetMapping("{gameId}/lobby")
  public ModelAndView lobbyByUid(@PathVariable String gameId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUid(gameId, uid);
    return lobby(gameAccessAuth, uid);
  }

  @GetMapping("{gameId}/lobby/{rulesetPlayerId}")
  public ModelAndView lobbyByPlayerId(
      @PathVariable String gameId, @PathVariable String rulesetPlayerId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUidAndPlayerId(gameId, uid, rulesetPlayerId);
    return lobby(gameAccessAuth, uid);
  }

  private ModelAndView lobby(GameStateService.GameAccessAuth gameAccessAuth, String uid) {
    gameStateService.checkLobbyAccess(gameAccessAuth);
    final var view = new ModelAndView("game/lobby");
    final var isOwner = gameAccessAuth.game().isOwner(uid);

    setGameViewInfo(view, gameAccessAuth, uid);
    view.addObject("isAllowedToLaunch", gameStateService.isAllowedToLaunchGame(gameAccessAuth, uid));
    view.addObject("players", getLobbyPlayers(gameAccessAuth, isOwner));
    return view;
  }

  @PostMapping("{gameId}/lobby")
  public String launchGameByUid(@PathVariable String gameId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUid(gameId, uid);
    return launchGame(gameAccessAuth, uid);
  }

  @PostMapping("{gameId}/lobby/{rulesetPlayerId}")
  public String launchGameByPlayerId(
      @PathVariable String gameId, @PathVariable String rulesetPlayerId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUidAndPlayerId(gameId, uid, rulesetPlayerId);
    return launchGame(gameAccessAuth, uid);
  }

  private String launchGame(GameStateService.GameAccessAuth gameAccessAuth, String uid) {
    final var updatedGame = gameStateService.launchGame(gameAccessAuth, uid);
    return GameRedirectHelper.redirectBasedOnGameState(updatedGame);
  }

  /**
   * Use first found role by uid if any
   * One uid could play for several roles
   */
  @GetMapping("{gameId}/playground")
  public ModelAndView playgroundByUid(@PathVariable String gameId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUid(gameId, uid);
    return playground(gameAccessAuth, uid);
  }

  @GetMapping("{gameId}/playground/{rulesetPlayerId}")
  public ModelAndView playgroundByPlayerId(
      @PathVariable String gameId, @PathVariable String rulesetPlayerId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUidAndPlayerId(gameId, uid, rulesetPlayerId);
    return playground(gameAccessAuth, uid);
  }

  private ModelAndView playground(GameStateService.GameAccessAuth gameAccessAuth, String uid) {
    gameStateService.checkPlaygroundAccess(gameAccessAuth);
    final var view = new ModelAndView("game/playground");
    setGameViewInfo(view, gameAccessAuth, uid);
    view.addObject("players", getLobbyPlayers(gameAccessAuth, false));
    return view;
  }

  @PostMapping("{gameId}/playground")
  public String controlGameByUid(@PathVariable String gameId, @RequestParam String action, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUid(gameId, uid);
    return controlGame(gameAccessAuth, uid, action);
  }

  @PostMapping("{gameId}/playground/{rulesetPlayerId}")
  public String controlGameByPlayerId(
      @PathVariable String gameId,
      @PathVariable String rulesetPlayerId,
      @RequestParam String action,
      Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUidAndPlayerId(gameId, uid, rulesetPlayerId);
    return controlGame(gameAccessAuth, uid, action);
  }

  private String controlGame(GameStateService.GameAccessAuth gameAccessAuth, String uid, String action) {
    gameStateService.checkPlaygroundAccess(gameAccessAuth);
    final GameStateService.GameAccessAuth updatedGame;
    if (action.equals("end-game")) {
      updatedGame = gameStateService.endGame(gameAccessAuth, uid);
    } else {
      throw new IllegalArgumentException("Unknown game action");
    }
    return GameRedirectHelper.redirectBasedOnGameState(updatedGame);
  }

  @GetMapping("{gameId}/credits")
  public ModelAndView creditsByUid(@PathVariable String gameId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUid(gameId, uid);
    return credits(gameAccessAuth, uid);
  }

  @GetMapping("{gameId}/credits/{rulesetPlayerId}")
  public ModelAndView creditsByPlayerId(
      @PathVariable String gameId, @PathVariable String rulesetPlayerId, Authentication auth) {
    final var uid = (String) auth.getPrincipal();
    final var gameAccessAuth = gameStateService.checkGameAccessAuthByUidAndPlayerId(gameId, uid, rulesetPlayerId);
    return credits(gameAccessAuth, uid);
  }

  private ModelAndView credits(GameStateService.GameAccessAuth gameAccessAuth, String uid) {
    gameStateService.checkCreditsAccess(gameAccessAuth);
    final var view = new ModelAndView("game/credits");
    setGameViewInfo(view, gameAccessAuth, uid);
    return view;
  }

  private List<ImmutableLobbyPlayer> getLobbyPlayers(
      GameStateService.GameAccessAuth gameAccessAuth, boolean withLinks) {
    final var myPlayer = gameAccessAuth.player();
    final var game = gameAccessAuth.game();
    final var ruleset = game.getRuleset().getRuleset();
    return gameStateService.getPlayers(game)
        .stream()
        .map(player -> {
          final var playerBuilder = ImmutableLobbyPlayer.builder();
          playerBuilder.name(player.getName());

          if (withLinks && (myPlayer == null || !player.getId().equals(myPlayer.getId()))) {
            var joinLinkUri = uriService.getBaseUriBuilder()
                .pathSegment("game", "{gameId}", "join", "{joinKey}")
                .encode()
                .buildAndExpand(Map.of("gameId", game.getId(), "joinKey", player.getJoinKey()));
            playerBuilder.joinLink(joinLinkUri.toUriString());
          }

          if (myPlayer != null && player.getId().equals(myPlayer.getId())) {
            playerBuilder.me(true);
          }

          ruleset.getPlayerById(player.getId().getRulesetPlayerId()).ifPresent(rulesetPlayer -> {
            playerBuilder.roleName(rulesetPlayer.roleName());
          });

          return playerBuilder.build();
        })
        .toList();
  }

  private void setGameViewInfo(ModelAndView view, GameStateService.GameAccessAuth gameAccessAuth, String uid) {
    final var myPlayer = gameAccessAuth.player();
    final var game = gameAccessAuth.game();
    final var ruleset = game.getRuleset().getRuleset();
    view.addObject("gameId", game.getId());
    view.addObject("isOwner", game.isOwner(uid));
    view.addObject("rulesetName", ruleset.name());
    if (myPlayer != null) {
      view.addObject("myName", myPlayer.getName());
      view.addObject("myRoleName", myPlayer.getName());
      view.addObject("myPlayerId", myPlayer.getId().getRulesetPlayerId());
      ruleset.getPlayerById(myPlayer.getId().getRulesetPlayerId()).ifPresent(rulesetPlayer -> {
        view.addObject("myRoleName", rulesetPlayer.roleName());
      });
    }
  }

  @ExceptionHandler(GameStateServiceErrors.GameNotFound.class)
  public ModelAndView notFoundError(Exception exception) {
    final var view = new ModelAndView("error");
    view.setStatus(HttpStatus.NOT_FOUND);
    view.addObject("error", exception.getMessage());
    return view;
  }

  @ExceptionHandler(GameStateServiceErrors.ForbiddenInCurrentGameState.class)
  public ModelAndView noAccessInCurrentStateError(GameStateServiceErrors.ForbiddenInCurrentGameState error) {
    final var view = new ModelAndView();
    try {
      view.setViewName(GameRedirectHelper.redirectBasedOnGameState(error.getGame()));
      return view;
    } catch (IllegalArgumentException e) {
      return unexpectedError(error);
    }
  }

  @ExceptionHandler(GameStateServiceErrors.Forbidden.class)
  public ModelAndView forbiddenError() {
    final var view = new ModelAndView("error");
    view.setStatus(HttpStatus.FORBIDDEN);
    view.addObject("error", "You don't have access to the game");
    return view;
  }

  @ExceptionHandler(GameStateServiceErrors.GameStateServiceException.class)
  public ModelAndView unexpectedError(Exception exception) {
    final var view = new ModelAndView("error");
    view.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    view.addObject("error", "Unexpected error");
    view.addObject("message", exception.getMessage());
    return view;
  }
}
