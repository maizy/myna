package dev.maizy.myna.ruleset;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableRuleset.class)
@JsonDeserialize(as = ImmutableRuleset.class)
public abstract class Ruleset {
  @Value.Default
  public String id() {
    return UUID.randomUUID().toString();
  }

  public abstract String name();

  @Nullable
  public abstract String description();

  @Nullable
  public abstract Zone gameZone();

  @Value.Default
  public ObjectsGroup rootObjects() {
    return ImmutableObjectsGroup.builder().build();
  }

  @Value.Default
  public List<Player> players() {
    return Collections.emptyList();
  }

  /**
   * unsupported - <a href="https://github.com/maizy/myna/issues/6">iss #6</a>
   */
  @Value.Default
  public List<ObjectsGroup> objectsStacks() {
    return Collections.emptyList();
  }

  /**
   * unsupported - <a href="https://github.com/maizy/myna/issues/11">iss #11</a>
   */
  @Value.Default
  public List<Zone> zones() {
    return Collections.emptyList();
  }

  public void validateOnCreation() {
    if (gameZone() == null) {
      throw new IllegalArgumentException("game zone not defined");
    }
  }

  public Optional<Player> getPlayerById(String playerId) {
    return players().stream().filter(p -> p.id().equals(playerId)).findAny();
  }
}
