package dev.maizy.myna.ruleset.state_change;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.List;

abstract public class StateChangeStrategy<T> {
  protected final List<T> states;
  public StateChangeStrategy(List<T> states) {
    this.states = states;
  }

  abstract public T nextState();
}
