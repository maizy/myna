package dev.maizy.myna.http.api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.RulesetRepository;
import dev.maizy.myna.ruleset.Ruleset;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiV1Prefix
@RestController
public class RulesetController {

  private final RulesetRepository rulesetRepository;

  public RulesetController(RulesetRepository rulesetRepository) {
    this.rulesetRepository = rulesetRepository;
  }

  @RequestMapping("/ruleset")
  public List<Ruleset> listRulesets() {
    return rulesetRepository.findAll();
  }
}
