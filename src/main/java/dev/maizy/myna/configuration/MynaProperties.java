package dev.maizy.myna.configuration;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2023
 * See LICENSE.txt for details.
 */

import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "myna")
@ConfigurationPropertiesScan
public class MynaProperties {

  public static class AdminApi {
    public AdminApi() {
      this.tokens = new HashSet<>();
    }

    private Set<String> tokens;

    public Set<String> getTokens() {
      return tokens;
    }

    public void setTokens(Set<String> tokens) {
      this.tokens = tokens;
    }
  }

  private AdminApi adminApi;

  public MynaProperties() {
    adminApi = new AdminApi();
  }

  public AdminApi getAdminApi() {
    return adminApi;
  }

  public void setAdminApi(AdminApi adminApi) {
    this.adminApi = adminApi;
  }
}
