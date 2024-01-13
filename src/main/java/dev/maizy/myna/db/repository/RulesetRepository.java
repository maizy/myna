package dev.maizy.myna.db.repository;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.RulesetEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RulesetRepository extends CrudRepository<RulesetEntity, String> {
  @Query(value = "select * from rulesets order by ruleset ->> 'name', id", nativeQuery = true)
  Page<RulesetEntity> findAll(Pageable pageable);

  @Query(value ="""
        select
          id,
          jsonb_build_object(
            'name', ruleset -> 'name',
            'description', ruleset -> 'description',
            'players', ruleset -> 'players'
          ) as ruleset
        from rulesets
        order by ruleset ->> 'name', id""", nativeQuery = true)
  List<RulesetEntity> findAllSummaries();
}
