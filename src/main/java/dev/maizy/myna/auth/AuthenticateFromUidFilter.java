package dev.maizy.myna.auth;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024-2025
 * See LICENSE.txt for details.
 */

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class AuthenticateFromUidFilter extends AbstractPreAuthenticatedProcessingFilter {

  private static final String NA = "NA";

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    var uid = (String) request.getSession().getAttribute(AutoGenerateUidFilter.UID_SESSION_KEY);
    if (uid != null) {
      return uid;
    } else {
     this.logger.warn("UID not found, it expected to be in the session");
    }
    return null;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return NA;
  }
}
