package dev.maizy.myna.db.repository;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.GamePlayerEntity;
import dev.maizy.myna.db.entity.GamePlayerId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface GamePlayerRepository extends CrudRepository<GamePlayerEntity, GamePlayerId> {
  List<GamePlayerEntity> findAllByIdGameIdOrderByRulesetOrderAscNameAsc(String gameId);

  Optional<GamePlayerEntity> findFirstByIdGameIdAndJoinKey(String gameId, String joinKey);

  Optional<GamePlayerEntity> findFirstByIdGameIdAndUidOrderByRulesetOrder(String gameId, String uid);

  Optional<GamePlayerEntity> findFirstByIdGameIdAndIdRulesetPlayerIdAndUid(
      String gameId, String rulesetPlayerId, String uid
  );
}
