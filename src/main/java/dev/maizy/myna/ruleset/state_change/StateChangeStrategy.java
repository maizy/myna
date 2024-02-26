package dev.maizy.myna.ruleset.state_change;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

public interface StateChangeStrategy<T> {
  T nextState();
}
