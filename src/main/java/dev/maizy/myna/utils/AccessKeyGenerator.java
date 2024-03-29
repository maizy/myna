package dev.maizy.myna.utils;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.security.SecureRandom;
import java.util.Random;

public class AccessKeyGenerator {

  private final Random random;

  private final char[] alphabet = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

  public static final int ACCESS_KEY_LENGTH = 42;

  public AccessKeyGenerator(Random random) {
    this.random = random;
  }

  public AccessKeyGenerator() {
    this(new SecureRandom());
  }

  public String generateAccessKey() {
    return random.ints(0, alphabet.length)
        .limit(ACCESS_KEY_LENGTH)
        .map(i -> alphabet[i])
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
