package dev.maizy.myna.http.admin_api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.repository.RulesetRepository;
import dev.maizy.myna.db.entity.RulesetEntity;
import dev.maizy.myna.dto.api.ApiErrors;
import dev.maizy.myna.dto.api.ImmutableApiObjectRef;
import dev.maizy.myna.ruleset.Ruleset;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AdminApiV1Prefix
@RestController
public class RulesetController {

  private final RulesetRepository rulesetRepository;

  public RulesetController(RulesetRepository rulesetRepository) {
    this.rulesetRepository = rulesetRepository;
  }

  @GetMapping("/ruleset")
  public ResponseEntity<Page<Ruleset>> listRulesets(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size
  ) {
    var data = rulesetRepository.findAll(PageRequest.of(page, size)).map(RulesetEntity::getRuleset);
    return ResponseEntity.ok(data);
  }

  @GetMapping("/ruleset/{id}")
  public ResponseEntity<?> getRuleset(@PathVariable String id) {
    var ruleset = rulesetRepository.findById(id);
    if (ruleset.isPresent()) {
      return ResponseEntity.ok(ruleset.get().getRuleset());
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrors.NotFound);
    }
  }

  @DeleteMapping("/ruleset/{id}")
  public ResponseEntity<?> deleteRuleset(@PathVariable String id) {
    try {
      rulesetRepository.deleteById(id);
      return ResponseEntity.noContent().build();
    } catch (EmptyResultDataAccessException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrors.NotFound);
    }
  }

  @PostMapping("/ruleset")
  public ResponseEntity<?> addRuleset(@RequestBody Ruleset ruleset) {
    try {
      ruleset.validateOnCreation();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrors.InvalidData);
    }
    final String id = ruleset.id();
    if (rulesetRepository.existsById(id)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrors.DuplicateId);
    } else {
      final var entity = new RulesetEntity();
      entity.setId(id);
      entity.setRuleset(ruleset);
      rulesetRepository.save(entity);
      return ResponseEntity.ok(ImmutableApiObjectRef.of(id));
    }
  }
}
