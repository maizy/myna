package dev.maizy.myna.dto.api;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

public interface ApiVersion {
  String version = "v1";
  String prefix = "/api/" + version;
}
