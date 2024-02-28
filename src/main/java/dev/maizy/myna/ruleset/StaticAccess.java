package dev.maizy.myna.ruleset;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableStaticAccess.class)
@JsonDeserialize(as = ImmutableStaticAccess.class)
public abstract class StaticAccess implements Access {

  public abstract static class Builder implements AccessBuilder { }

  @Value.Default
  public boolean visibleForAll() {
    return false;
  }

  @Value.Default
  public boolean ownedForAll() {
    return false;
  }

  public abstract Set<Ref> owners();
  public abstract Set<Ref> viewers();

  @Value.Check
  void check() {
    if (visibleForAll() && !viewers().isEmpty()) {
      throw new IllegalArgumentException("viewers shouldn't be specified with visibleForAll flag");
    }
    if (ownedForAll() && !owners().isEmpty()) {
      throw new IllegalArgumentException("viewers shouldn't be specified with visibleForAll flag");
    }
  }

  @Override
  public boolean isOwner(Player player) {
    return ownedForAll() || owners().contains(player.ref());
  }

  @Override
  public boolean isVisible(Player player) {
    return visibleForAll() || viewers().contains(player.ref());
  }
}
