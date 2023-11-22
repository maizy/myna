package dev.maizy.myna.db;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.Ruleset;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RulesetRepository extends CrudRepository<Ruleset, String> {
  @Query(value = "select * from rulesets order by ruleset ->> 'name', id", nativeQuery = true)
  List<Ruleset> findAll();
}
