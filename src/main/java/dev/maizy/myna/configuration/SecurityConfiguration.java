package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023-2025
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.auth.AuthenticateFromUidFilter;
import dev.maizy.myna.auth.AuthentificateByUidAuthenticationManager;
import dev.maizy.myna.auth.AutoGenerateUidFilter;
import dev.maizy.myna.auth.PreconfiguredTokensFilter;
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
import org.springframework.security.web.context.NullSecurityContextRepository;

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
    final var adminApiAuthFilter = new PreconfiguredTokensFilter();
    adminApiAuthFilter.setAuthenticationManager(adminAuthManager);
    adminApiAuthFilter.setSecurityContextRepository(new NullSecurityContextRepository());

    return http
        .securityMatcher(AdminApiVersion.prefix + "/**")
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .logout(logout -> logout.disable())
        .httpBasic(basic -> basic.disable())
        .requestCache(cache -> cache.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .addFilter(adminApiAuthFilter)
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .anonymous(anonymous -> anonymous.disable())
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain withSessionFilterChain(HttpSecurity http) throws Exception {
    final var httpSecurity = http.securityMatcher(UrisWithSession.mvcRulesWithCsrfProtection);
    configureHttpSecurityWithSession(httpSecurity);
    return http.build();
  }

  @Bean
  @Order(3)
  public SecurityFilterChain withSessionFilterChainWithoutCsrf(HttpSecurity http) throws Exception {
    final var httpSecurity = http
        .securityMatcher(UrisWithSession.mvcRulesWithoutCsrfProtection)
        .csrf(csrf -> csrf.disable());
    configureHttpSecurityWithSession(httpSecurity);
    return http.build();
  }

  private void configureHttpSecurityWithSession(HttpSecurity httpSecurity) throws Exception {
    final var gameAuthFilter = new AuthenticateFromUidFilter();
    gameAuthFilter.setAuthenticationManager(new AuthentificateByUidAuthenticationManager());
    gameAuthFilter.setSecurityContextRepository(new NullSecurityContextRepository());
    httpSecurity
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .formLogin(form -> form.disable())
        .logout(logout -> logout.disable())
        .httpBasic(basic -> basic.disable())
        .requestCache(cache -> cache.disable())
        .addFilter(gameAuthFilter)
        .addFilterBefore(new AutoGenerateUidFilter(), AuthenticateFromUidFilter.class)
        .anonymous(anonymous -> anonymous.disable());
  }

  @Bean
  public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .logout(logout -> logout.disable())
        .httpBasic(basic -> basic.disable())
        .requestCache(cache -> cache.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .build();
  }
}
