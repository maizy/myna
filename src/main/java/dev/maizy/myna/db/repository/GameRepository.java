package dev.maizy.myna.db.repository;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.db.entity.GameEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<GameEntity, String> {
  Stream<GameEntity> streamAllByOwnerUidOrderByCreatedAtDesc(String uid);
  List<GameEntity> getAllByOwnerUidOrderByCreatedAtDesc(String uid);

  @Query("SELECT game FROM GameEntity game LEFT JOIN FETCH game.ruleset WHERE game.id = ?1")
  Optional<GameEntity> findByIdAndFetchRuleset(String gameId);
}
