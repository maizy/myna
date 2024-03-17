package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.ruleset.Player;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayerWithStatus.class)
@JsonDeserialize(as = ImmutablePlayerWithStatus.class)
public abstract class PlayerWithStatus {

  public enum PlayerStatus { playing, absent }

  @Value.Parameter
  public abstract Player player();

  @Value.Parameter
  public abstract PlayerStatus status();

}
