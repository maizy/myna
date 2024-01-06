package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
  @RequestMapping("")
  public String index() {
    return "index";
  }
}
