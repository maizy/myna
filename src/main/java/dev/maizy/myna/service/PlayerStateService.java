package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.game_message.event.ImmutablePlayerWithStatus;
import dev.maizy.myna.game_message.event.ImmutablePlayersState;
import dev.maizy.myna.game_message.event.PlayersState;
import dev.maizy.myna.ruleset.Player;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlayerStateService {

  private final StringRedisTemplate redisTemplate;

  public PlayerStateService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void setPlayerConnected(String gameId, String rulesetRoleId, String uid) {
    redisTemplate.opsForHash().put(RedisKeys.playersState(gameId), rulesetRoleId, uid);
  }

  public void setPlayerDisconnected(String gameId, String rulesetRoleId) {
    redisTemplate.opsForHash().delete(RedisKeys.playersState(gameId), rulesetRoleId);
  }

  public PlayersState getPlayersState(String gameId, List<Player> players) {
    final var states = redisTemplate.opsForHash().entries(RedisKeys.playersState(gameId));

    final var playersWithStatus = players.stream().map(player -> {
      final var status = (states.get(player.id()) != null)
          ? PlayersState.PlayerStatus.playing
          : PlayersState.PlayerStatus.absent;
      return ImmutablePlayerWithStatus.of(player, status);
    }).toArray(PlayersState.PlayerWithStatus[]::new);

    return ImmutablePlayersState.builder()
        .gameId(gameId)
        .addPlayers(playersWithStatus)
        .build();
  }
}
