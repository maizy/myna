package dev.maizy.myna.ruleset.state_change;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.List;

public interface WithStates<S> {
  List<S> states();
}
