package dev.maizy.myna.surface;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Colors {

  white(ImmutableColor.of("ffffff")),
  black(ImmutableColor.of("000000")),
  red(ImmutableColor.of("f04e32")),
  blue(ImmutableColor.of("3b74ba")),
  magenta(ImmutableColor.of("f0609e")),
  yellow(ImmutableColor.of("fbad18"));

  private final Color hex;

  Colors(Color hex) {
    this.hex = hex;
  }

  public Color hex() {
    return hex;
  }

  private static final Map<String, Color> byCode = new HashMap<>();

  static {
    for (Colors c : values()) {
      byCode.put(c.toString(), c.hex);
    }
  }

  public static Optional<Color> byCode(String code) {
    return Optional.of(byCode.get(code));
  }
}
