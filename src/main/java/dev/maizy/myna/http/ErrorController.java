package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

  @RequestMapping("/error")
  public String error(Model model, HttpServletRequest request) {
    final var error = (Integer) request.getAttribute("javax.servlet.error.status_code");
    model.addAttribute("status", error != null ? error : "500");
    model.addAttribute("message", request.getAttribute("javax.servlet.error.message"));
    return "error";
  }
}
