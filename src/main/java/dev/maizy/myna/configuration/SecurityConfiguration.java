package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.auth.PreconfiguratedTokensFilter;
import dev.maizy.myna.dto.api.AdminApiVersion;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final AuthenticationManager adminAuthManager;

  public SecurityConfiguration(@Qualifier("admin_auth_manager") AuthenticationManager adminAuthManager) {
    this.adminAuthManager = adminAuthManager;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    var adminAuthFilter = new PreconfiguratedTokensFilter();
    adminAuthFilter.setAuthenticationManager(adminAuthManager);

    http
      .antMatcher(AdminApiVersion.prefix + "/**")
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(adminAuthFilter)
        .authorizeRequests()
        .anyRequest().authenticated();

    return http.build();
  }
}
