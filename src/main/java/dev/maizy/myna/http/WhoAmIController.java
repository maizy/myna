package dev.maizy.myna.http;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.auth.AutoGenerateUidFilter;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WhoAmIController {
  @RequestMapping("/whoami")
  public String index(HttpSession session, Model model) {
    model.addAttribute("uid", session.getAttribute(AutoGenerateUidFilter.UID_SESSION_KEY));
    return "whoami";
  }
}
