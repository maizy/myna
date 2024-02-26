package dev.maizy.myna.configuration;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class UrisWithSession {
  public static final String[] resources = {"/whoami"};
  private static final Set<String> resourcesSet = new HashSet<>(Arrays.asList(resources));

  public static final String[] resourcesPrefixes = {"/game", "/games", "/ws"};
  public static final String[] mvcRules = Stream.concat(
      Arrays.stream(resourcesPrefixes).map(p -> p + "/**"),
      Arrays.stream(resources)
  ).toArray(String[]::new);

  /*
   * Primitive URI matcher
   */
  public static boolean isUriWithSession(String uri) {
    final var noSlashUri = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    if (resourcesSet.contains(noSlashUri)) {
      return true;
    }
    return Arrays.stream(resourcesPrefixes).anyMatch(p -> noSlashUri.equals(p) || uri.startsWith(p + "/"));
  }

  private UrisWithSession() {
  }
}
