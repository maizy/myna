package dev.maizy.myna.ruleset.state_change;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.List;

public class Random<S> extends StateChangeStrategy<S> {

  final private java.util.Random rand = new java.util.Random();

  public Random(List<S> states) {
    super(states);
  }

  public S nextState() {
    return states.get(rand.nextInt(states.size()));
  }
}
