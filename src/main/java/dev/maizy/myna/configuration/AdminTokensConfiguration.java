package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.auth.PreconfiguratedTokensAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

@Configuration
public class AdminTokensConfiguration {

  @Bean("admin_auth_manager")
  public AuthenticationManager adminAuthManager(MynaProperties mynaProps) {
    return new PreconfiguratedTokensAuthenticationManager(mynaProps.getAdminApi().getTokens());
  }

}
