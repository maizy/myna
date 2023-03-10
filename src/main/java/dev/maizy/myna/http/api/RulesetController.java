package dev.maizy.myna.http.api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiV1Prefix
@RestController
public class RulesetController {
  @RequestMapping("/ruleset")
  public String listRulesets() {
    return "todo";
  }
}
