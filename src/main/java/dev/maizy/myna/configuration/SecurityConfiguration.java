package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  // FIXME
  /*private final AuthenticationManager adminAuthManager;

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
  public SecurityFilterChain withSessionFilterChain(HttpSecurity http) throws Exception {
    final var httpSecurity = http.requestMatchers()
        .mvcMatchers(UrisWithSession.mvcRulesWithCsrfProtection)
        .and();
    configureHttpSecurityWithSession(httpSecurity);
    return http.build();
  }

  @Bean
  @Order(3)
  public SecurityFilterChain withSessionFilterChainWithoutCsrf(HttpSecurity http) throws Exception {

    final var httpSecurity = http.requestMatchers()
        .mvcMatchers(UrisWithSession.mvcRulesWithoutCsrfProtection)
        .and().csrf().disable();
    configureHttpSecurityWithSession(httpSecurity);
    return http.build();
  }

  private void configureHttpSecurityWithSession(HttpSecurity httpSecurity) throws Exception {
    final var gameAuthFilter = new AuthentificateFromUidFilter();
    gameAuthFilter.setAuthenticationManager(new AuthentificateByUidAuthenticationManager());
    httpSecurity
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().sessionManagement().sessionFixation().none()
        .and()
        .addFilter(gameAuthFilter)
        .addFilterBefore(new AutoGenerateUidFilter(), AuthentificateFromUidFilter.class)
        .anonymous().disable();
  }

  @Bean
  public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
    return http
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
        .build();
  }*/
}
