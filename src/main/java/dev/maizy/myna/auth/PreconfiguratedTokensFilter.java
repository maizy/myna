package dev.maizy.myna.auth;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class PreconfiguratedTokensFilter extends AbstractPreAuthenticatedProcessingFilter {

  static public String AUTHORIZATION_HEADER = "Authorization";
  static public String BEARER_PREFIX = "Bearer ";
  static public Object NA = new Object();

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
