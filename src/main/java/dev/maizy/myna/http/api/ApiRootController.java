package dev.maizy.myna.http.api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.dto.api.ApiVersion;
import dev.maizy.myna.dto.api.ImmutableApiObject;
import dev.maizy.myna.dto.api.ImmutableRoot;
import dev.maizy.myna.dto.api.Root;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiV1Prefix
@RestController
public class ApiRootController {
  @RequestMapping({"/", ""})
  public Root apiRoot() {
    return ImmutableRoot.builder()
        .version(ApiVersion.version)
        .addObjects(
            ImmutableApiObject.builder()
                .url(ApiVersion.prefix + "/ruleset")
                .description("Define objects and rules for a game")
                .build()
        )
        .build();
  }
}
