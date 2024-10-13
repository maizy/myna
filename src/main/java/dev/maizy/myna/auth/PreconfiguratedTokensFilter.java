package dev.maizy.myna.auth;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class PreconfiguratedTokensFilter extends AbstractPreAuthenticatedProcessingFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final Object NA = new Object();

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    var headerValue = request.getHeader(AUTHORIZATION_HEADER);
    if (headerValue != null && headerValue.startsWith(BEARER_PREFIX) && headerValue.length() > BEARER_PREFIX.length()) {
      return headerValue.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return NA;
  }
}
