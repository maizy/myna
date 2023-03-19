package dev.maizy.myna.http.api;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.dto.api.ApiObject;
import dev.maizy.myna.dto.api.ApiVersion;
import dev.maizy.myna.dto.api.Root;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiV1Prefix
@RestController
public class ApiRootController {
  @RequestMapping({"/", ""})
  public Root apiRoot() {
    return new Root(
        ApiVersion.version,
        List.of(
            new ApiObject(ApiVersion.prefix + "/ruleset", "Define objects and rules for a game")
        )
    );
  }
}
