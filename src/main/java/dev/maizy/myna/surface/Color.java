package dev.maizy.myna.surface;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.regex.Pattern;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableColor.class)
@JsonDeserialize(as = ImmutableColor.class)
public abstract class Color {
  final private Pattern format = Pattern.compile("^[0-9a-f]{6}$");

  @Value.Parameter
  public abstract String hex();

  @Value.Check
  void check() {
    final var hex = hex();
    if (!format.matcher(hex).matches()) {
      throw new IllegalArgumentException("hex should be in hex format, given '" + hex + "'");
    }
  }
}
