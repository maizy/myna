package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

  @RequestMapping("/error")
  public String error(Model model, HttpServletRequest request) {
    final var error = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    final var statusCode = error != null ? error.toString() : "500";
    model.addAttribute("status", statusCode);
    model.addAttribute("error", "Error " + statusCode);
    model.addAttribute("message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
    return "error";
  }
}
