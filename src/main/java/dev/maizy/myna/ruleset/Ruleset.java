package dev.maizy.myna.ruleset;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(depluralize = true)
@JsonSerialize(as = ImmutableRuleset.class)
@JsonDeserialize(as = ImmutableRuleset.class)
public abstract class Ruleset {
  @Value.Default
  public String id() {
    return UUID.randomUUID().toString();
  }

  public abstract String name();

  public abstract Zone gameZone();

  @Value.Default
  public ObjectsGroup rootObjects() {
    return ImmutableObjectsGroup.builder().build();
  }

  @Value.Default
  public List<ObjectsGroup> objectsStacks() {
    return Collections.emptyList();
  }

  @Value.Default
  public List<Player> players() {
    return Collections.emptyList();
  }

  @Value.Default
  public List<Zone> zones() {
    return Collections.emptyList();
  }
}
