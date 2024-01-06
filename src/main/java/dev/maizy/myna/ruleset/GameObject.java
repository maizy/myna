package dev.maizy.myna.ruleset;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.ruleset.state_change.WithStates;
import dev.maizy.myna.surface.Size;
import java.util.List;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGameObject.class)
@JsonDeserialize(as = ImmutableGameObject.class)
public abstract class GameObject implements WithStates<ObjectState> {
  @Value.Default
  public String id() {
    return UUID.randomUUID().toString();
  }

  @Value.Check
  void check() {
    if(states().isEmpty()) {
      throw new IllegalArgumentException("game object should have at least one state");
    }
  }

  /**
   * only one state supported for now - <a href="https://github.com/maizy/myna/issues/14">iss #14</a>
   */
  public abstract List<ObjectState> states();

  public abstract Size size();

  /**
   * only fixed state supported - <a href="https://github.com/maizy/myna/issues/14">iss #14</a>
   */
  @Value.Default
  public ObjectStateChangeStrategy stateChangeStrategy() {
    return ObjectStateChangeStrategy.fixed;
  }
}
