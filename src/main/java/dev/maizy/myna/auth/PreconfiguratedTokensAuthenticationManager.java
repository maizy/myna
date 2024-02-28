package dev.maizy.myna.auth;

import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class PreconfiguratedTokensAuthenticationManager implements AuthenticationManager {

  private final Set<String> allowedTokens;

  public PreconfiguratedTokensAuthenticationManager(Set<String> allowedTokens) {
    this.allowedTokens = allowedTokens;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    var tokenObject = authentication.getPrincipal();
    if (tokenObject instanceof String token) {
      if (allowedTokens.contains(token)) {
        authentication.setAuthenticated(true);
        return authentication;
      }
    }
    throw new BadCredentialsException("Wrong API Token");
  }
}
