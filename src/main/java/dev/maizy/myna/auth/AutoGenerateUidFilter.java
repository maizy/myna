package dev.maizy.myna.auth;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.GenericFilterBean;

public class AutoGenerateUidFilter extends GenericFilterBean {

  public static final String UID_SESSION_KEY = "uid";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    var attrs = RequestContextHolder.currentRequestAttributes();
    if (attrs instanceof ServletRequestAttributes servletAttrs) {
      var session = servletAttrs.getRequest().getSession();
      if (session.getAttribute(UID_SESSION_KEY) == null) {
        session.setAttribute(UID_SESSION_KEY, UUID.randomUUID().toString());
      }
    }
    chain.doFilter(request, response);
  }
}
