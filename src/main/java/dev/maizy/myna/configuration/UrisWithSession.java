package dev.maizy.myna.configuration;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024-2025
 * See LICENSE.txt for details.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class UrisWithSession {
  public static final String[] resourcesWithCsrfProtection = {};
  public static final String[] resourcesPrefixesWithCsrfProtection = {"/game", "/games"};

  public static final String[] resourcesWithoutCsrfProtection = {"/whoami"};
  public static final String[] resourcesPrefixesWithoutCsrfProtection = {"/ws"};

  public static final String[] mvcRulesWithCsrfProtection = Stream.concat(
      Arrays.stream(resourcesPrefixesWithCsrfProtection).map(p -> p + "/**"),
      Arrays.stream(resourcesWithCsrfProtection)
  ).toArray(String[]::new);

  public static final String[] mvcRulesWithoutCsrfProtection = Stream.concat(
      Arrays.stream(resourcesPrefixesWithoutCsrfProtection).map(p -> p + "/**"),
      Arrays.stream(resourcesWithoutCsrfProtection)
  ).toArray(String[]::new);

  private static final Set<String> resourcesSet = new HashSet<>(
      Stream.concat(
          Arrays.stream(resourcesWithCsrfProtection),
          Arrays.stream(resourcesWithoutCsrfProtection)
      ).toList()
  );

  /*
   * Primitive URI matcher
   */
  public static boolean isUriWithSession(String uri) {
    final var noSlashUri = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    if (resourcesSet.contains(noSlashUri)) {
      return true;
    }
    return Stream.concat(
        Arrays.stream(resourcesPrefixesWithCsrfProtection),
        Arrays.stream(resourcesPrefixesWithoutCsrfProtection)
    ).anyMatch(p -> noSlashUri.equals(p) || uri.startsWith(p + "/"));
  }

  private UrisWithSession() {
  }
}
