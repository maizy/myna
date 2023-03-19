package dev.maizy.myna.ruleset.state_change;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Sequential<S> extends StateChangeStrategy<S> {

  final private Iterator<S> statesIterator;

  public Sequential(List<S> states) {
    super(states);
    statesIterator = Stream.generate(() -> states).flatMap(List::stream).iterator();
  }

  public S nextState() {
    return statesIterator.next();
  }
}
