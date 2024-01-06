package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.dto.ImmutableStatus;
import dev.maizy.myna.dto.Status;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
  @RequestMapping("/status")
  public Status index() {
    return ImmutableStatus.of("ok");
  }
}
