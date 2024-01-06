package dev.maizy.myna.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AuthentificateByUidAuthenticationManager implements AuthenticationManager {

  public AuthentificateByUidAuthenticationManager() {
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (authentication.getPrincipal() != null) {
        authentication.setAuthenticated(true);
        return authentication;
    }
    throw new BadCredentialsException("UID is missing");
  }
}
