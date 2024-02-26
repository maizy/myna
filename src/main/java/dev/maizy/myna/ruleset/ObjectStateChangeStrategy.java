package dev.maizy.myna.ruleset;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.ruleset.state_change.Fixed;
import dev.maizy.myna.ruleset.state_change.Random;
import dev.maizy.myna.ruleset.state_change.RandomFixed;
import dev.maizy.myna.ruleset.state_change.Sequential;
import dev.maizy.myna.ruleset.state_change.ShuffledSequential;

public enum ObjectStateChangeStrategy {

  random_fixed(o -> new RandomFixed<>(o.states())),
  fixed(o -> new Fixed<>(o.states())),
  sequential(o -> new Sequential<>(o.states())),
  shuffled_sequential(o -> new ShuffledSequential<>(o.states())),
  random(o -> new Random<>(o.states()));

  private final ObjectStrateStrategyBuilder stategyBuilder;

  public ObjectStrateStrategyBuilder getStategyBuilder() {
    return stategyBuilder;
  }

  ObjectStateChangeStrategy(ObjectStrateStrategyBuilder stategyBuilder) {
    this.stategyBuilder = stategyBuilder;
  }
}
