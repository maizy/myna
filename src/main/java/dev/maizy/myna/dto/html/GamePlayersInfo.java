package dev.maizy.myna.dto.html;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.db.entity.RulesetEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGamePlayersInfo.class)
@JsonDeserialize(as = ImmutableGamePlayersInfo.class)
public abstract class GamePlayersInfo {

  public abstract String id();

  public abstract List<GamePlayer> players();

  public static Map<String, ? extends GamePlayersInfo> fromRulesetEntities(Collection<RulesetEntity> rulesets) {
    return rulesets
        .stream()
        .map(ruleset -> {
          var infoBuilder = ImmutableGamePlayersInfo.builder().id(ruleset.getId());
          ruleset.getRuleset().players().forEach(player -> infoBuilder.addPlayer(
              ImmutableGamePlayer.of(player.id(), player.roleName())
          ));
          return infoBuilder.build();
        })
        .collect(Collectors.toMap(GamePlayersInfo::id, Function.identity()));
  }
}
