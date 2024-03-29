package dev.maizy.myna.ruleset.state_change;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.List;

public class Fixed<S> implements StateChangeStrategy<S> {
  private final S fixedState;

  public Fixed(List<S> states) {
    fixedState = states.get(0);
  }

  public S nextState() {
    return fixedState;
  }
}
