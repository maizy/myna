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
import org.springframework.lang.Nullable;

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

  public static class BaseUri {
    private String scheme;
    private String host;

    @Nullable
    private Integer port;

    public BaseUri() {
    }

    public String getScheme() {
      return scheme;
    }

    public void setScheme(String scheme) {
      this.scheme = scheme;
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    @Nullable
    public Integer getPort() {
      return port;
    }

    public void setPort(@Nullable Integer port) {
      this.port = port;
    }
  }

  private AdminApi adminApi;

  private BaseUri baseUri;

  public MynaProperties() {
    adminApi = new AdminApi();
  }

  public AdminApi getAdminApi() {
    return adminApi;
  }

  public void setAdminApi(AdminApi adminApi) {
    this.adminApi = adminApi;
  }

  public BaseUri getBaseUri() {
    return baseUri;
  }

  public void setBaseUri(BaseUri baseUri) {
    this.baseUri = baseUri;
  }
}
