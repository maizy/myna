package dev.maizy.myna.dto.html;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
public abstract class LobbyPlayer {
  public abstract String name();
  public abstract String roleName();
  @Nullable
  public abstract String joinLink();

  @Value.Default
  public boolean me() {
    return false;
  }
}
