package dev.maizy.myna.db;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.ruleset.Ruleset;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RulesetRepository {
  public List<Ruleset> findAll() {
    return Collections.emptyList();
  }
}
