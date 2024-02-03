package dev.maizy.myna.utils;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class AccessKeyGeneratorTest {

  @Test
  public void testGenerate() {
    final var generator = new AccessKeyGenerator();
    final var key = generator.generateAccessKey();
    assertThat(key).matches("[a-zA-Z0-9]{42}");
  }

  @Test
  public void randomnessTest() {
    final var generator = new AccessKeyGenerator();
    final var keys = IntStream.range(0, 1000).mapToObj(i -> generator.generateAccessKey()).toList();
    assertThat(keys).doesNotHaveDuplicates();
  }
}
