package dev.maizy.myna.ruleset.state_change;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.List;

public class RandomFixed<S> implements StateChangeStrategy<S> {
  private final S fixedState;

  public RandomFixed(List<S> states) {
    var rand = new java.util.Random();
    fixedState = states.get(rand.nextInt(states.size()));
  }

  public S nextState() {
    return fixedState;
  }
}
