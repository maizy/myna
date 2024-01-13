package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.repository.RulesetRepository;
import dev.maizy.myna.dto.html.ImmutableGamePlayersInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/games")
public class GamesController {

  final private RulesetRepository rulesetRepository;

  public GamesController(RulesetRepository rulesetRepository) {
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

  @PostMapping("/create")
  public String processCreateForm(Model model) {
    final var error = "TODO";
    model.addAttribute("error", error);
    return create(model);
  }

}
