package dev.maizy.myna.http.admin_api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.RulesetRepository;
import dev.maizy.myna.ruleset.Ruleset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AdminApiV1Prefix
@RestController
public class RulesetController {

  private final RulesetRepository rulesetRepository;

  public RulesetController(RulesetRepository rulesetRepository) {
    this.rulesetRepository = rulesetRepository;
  }

  @RequestMapping("/ruleset")
  public ResponseEntity<Page<Ruleset>> listRulesets(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size
  ) {
    var data = rulesetRepository.findAll(PageRequest.of(page, size)).map(dev.maizy.myna.db.entity.Ruleset::getRuleset);
    return ResponseEntity.ok(data);
  }
}
