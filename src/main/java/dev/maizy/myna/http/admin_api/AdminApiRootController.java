package dev.maizy.myna.http.admin_api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.dto.api.AdminApiVersion;
import dev.maizy.myna.dto.api.ApiRoot;
import dev.maizy.myna.dto.api.ImmutableApiObject;
import dev.maizy.myna.dto.api.ImmutableApiRoot;
import dev.maizy.myna.service.UriService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AdminApiV1Prefix
@RestController
public class AdminApiRootController {

  final UriService uriService;

  public AdminApiRootController(UriService uriService) {
    this.uriService = uriService;
  }

  @RequestMapping({"/", ""})
  public ApiRoot apiRoot() {
    return ImmutableApiRoot.builder()
        .version(AdminApiVersion.version)
        .addObject(
            ImmutableApiObject.builder()
                .url(uriService.getContextPath() + AdminApiVersion.prefix + "/ruleset")
                .description("Define objects and rules for a game")
                .build()
        )
        .build();
  }
}
