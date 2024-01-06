package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.auth.AuthentificateByUidAuthenticationManager;
import dev.maizy.myna.auth.AuthentificateFromUidFilter;
import dev.maizy.myna.auth.AutoGenerateUidFilter;
import dev.maizy.myna.auth.PreconfiguratedTokensFilter;
import dev.maizy.myna.dto.api.AdminApiVersion;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
  @Order(1)
  public SecurityFilterChain adminApiFilterChain(HttpSecurity http) throws Exception {
    final var adminApiAuthFilter = new PreconfiguratedTokensFilter();
    adminApiAuthFilter.setAuthenticationManager(adminAuthManager);

    http.mvcMatcher(AdminApiVersion.prefix + "/**")
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(adminApiAuthFilter)
        .authorizeRequests()
        .anyRequest().authenticated()
        .and().anonymous().disable();

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain gameFilterChain(HttpSecurity http) throws Exception {
    final var gameAuthFilter = new AuthentificateFromUidFilter();
    gameAuthFilter.setAuthenticationManager(new AuthentificateByUidAuthenticationManager());

    http.requestMatchers().mvcMatchers("/whoami", "/game/**", "/games/**")
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().sessionManagement().sessionFixation().none()
        .and()
        .addFilter(gameAuthFilter)
        .addFilterBefore(new AutoGenerateUidFilter(), AuthentificateFromUidFilter.class)
        .anonymous().disable();
    return http.build();
  }

  @Bean
  public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
    return http
        .csrf().disable()
        .build();
  }
}
