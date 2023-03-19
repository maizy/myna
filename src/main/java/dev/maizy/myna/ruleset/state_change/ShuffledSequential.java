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

public class ShuffledSequential<S> extends StateChangeStrategy<S> {

  final private Iterator<S> statesIterator;

  public ShuffledSequential(List<S> states) {
    super(states);
    var shuffled = new ArrayList<>(states);
    Collections.shuffle(shuffled);
    statesIterator = Stream.generate(() -> states).flatMap(List::stream).iterator();
  }

  public S nextState() {
    return statesIterator.next();
  }
}
