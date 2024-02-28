package dev.maizy.myna.ruleset.state_change;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.List;

public class Random<S> implements StateChangeStrategy<S> {

  private final java.util.Random rand = new java.util.Random();
  private final List<S> states;

  public Random(List<S> states) {
    this.states = states;
  }

  public S nextState() {
    return states.get(rand.nextInt(states.size()));
  }
}
