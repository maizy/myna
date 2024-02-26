package dev.maizy.myna.ruleset.state_change;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ShuffledSequential<S> implements StateChangeStrategy<S> {

  private final Iterator<S> statesIterator;

  public ShuffledSequential(List<S> states) {
    var shuffled = new ArrayList<>(states);
    Collections.shuffle(shuffled);
    statesIterator = Stream.generate(() -> states).flatMap(List::stream).iterator();
  }

  public S nextState() {
    return statesIterator.next();
  }
}
