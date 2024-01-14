package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.GameEntity;
import dev.maizy.myna.db.repository.RulesetRepository;
import dev.maizy.myna.dto.html.ImmutableGamePlayersInfo;
import dev.maizy.myna.http.form.CreateGameForm;
import dev.maizy.myna.http.helper.GameRedirectHelper;
import dev.maizy.myna.service.GameStateService;
import dev.maizy.myna.service.GameStateServiceErrors;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/games")
public class GamesController {

  private static final Logger log = LoggerFactory.getLogger(GamesController.class);

  final private GameStateService gameStateService;
  final private RulesetRepository rulesetRepository;

  public GamesController(GameStateService gameStateService, RulesetRepository rulesetRepository) {
    this.gameStateService = gameStateService;
    this.rulesetRepository = rulesetRepository;
  }

  @GetMapping("/create")
  public String create(Model model) {
    final var rulesets = rulesetRepository.findAllSummaries();
    model.addAttribute("rulesets", rulesets);
    model.addAttribute(
        "playersInfo",
        ImmutableGamePlayersInfo.fromRulesetEntities(rulesets)
    );
    return "games/create";
  }

  @PostMapping(path = "/create")
  public String processCreateForm(Model model, Authentication auth, CreateGameForm form) {
    final GameEntity newGame;
    try {
      newGame = gameStateService.createGame(
          form.getRulesetId(), (String) auth.getPrincipal(), form.getPlayerName(),
          Optional.of(form.getMe()).filter(v -> !v.isEmpty())
      );
    } catch (GameStateServiceErrors.GameStateChangeError stateChangeError) {
      log.error("Unable to create game", stateChangeError);
      model.addAttribute("error", stateChangeError.getMessage());
      return create(model);
    }
    return GameRedirectHelper.redirectBasedOnGameState(newGame);
  }

}
