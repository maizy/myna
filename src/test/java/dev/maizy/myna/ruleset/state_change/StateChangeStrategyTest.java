package dev.maizy.myna.ruleset.state_change;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class StateChangeStrategyTest {

  private record State(String code) {
  }
  private final List<State> testStates = List.of(new State("a"), new State("b"), new State("c"));

  @Test
  void testFixedStrategy() {
    var strategy = new Fixed<>(testStates);
    assertEquals(strategy.nextState().code, "a");
    assertEquals(strategy.nextState().code, "a");
  }

  @Test
  void testRandomFixedStrategy() {
    var strategy = new RandomFixed<>(testStates);
    var first = strategy.nextState();
    assertThat(first).isIn(testStates);
    assertEquals(strategy.nextState(), first);
    assertEquals(strategy.nextState(), first);
  }

  @Test
  void testSequentialStrategy() {
    var strategy = new Sequential<>(testStates);
    assertEquals(strategy.nextState().code, "a");
    assertEquals(strategy.nextState().code, "b");
    assertEquals(strategy.nextState().code, "c");
    assertEquals(strategy.nextState().code, "a");
    assertEquals(strategy.nextState().code, "b");
    assertEquals(strategy.nextState().code, "c");
  }

  @Test
  void testShuffledSequentialStrategy() {
    var strategy = new ShuffledSequential<>(testStates);
    final State first = strategy.nextState();
    final State second = strategy.nextState();
    final State third = strategy.nextState();

    assertThat(first).isIn(testStates);
    assertThat(second).isIn(testStates);
    assertThat(third).isIn(testStates);

    assertThat(first).isNotIn(second, third);
    assertThat(second).isNotIn(first, third);
    assertThat(third).isNotIn(first, second);

    assertEquals(strategy.nextState(), first);
    assertEquals(strategy.nextState(), second);
    assertEquals(strategy.nextState(), third);
  }
}
