package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Integer.MIN_VALUE)
public class SessionFilterConfiguration extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                  FilterChain filterChain) throws ServletException, IOException {
    // disable session filter to prevent checking session in the storage when the session cookie is provided
    // seems like it's only available workarond, see https://github.com/spring-projects/spring-session/issues/244
    // there is no way to configure springSessionRepositoryFilter urls matcher
    if (!UrisWithSession.isUriWithSession(httpRequest.getRequestURI())) {
      httpRequest.setAttribute("org.springframework.session.web.http.SessionRepositoryFilter.FILTERED", Boolean.TRUE);
    }
    filterChain.doFilter(httpRequest, httpResponse);
  }
}
