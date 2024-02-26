package dev.maizy.myna.ruleset;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.ruleset.state_change.StateChangeStrategy;
import java.util.function.Function;

public interface ObjectStrateStrategyBuilder extends Function<GameObject, StateChangeStrategy<ObjectState>> {
}
