package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.configuration.MynaProperties;
import javax.servlet.ServletContext;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UriService {

  private final ServletContext servletContext;

  private int servletPort = 12813;
  private final String servletHost;

  private final String scheme;

  private final String host;
  private Integer port = null;
  private boolean updatePortFromServletPort = false;

  public UriService(MynaProperties mynaProperties, ServletContext servletContext, Environment env) {
    this.servletContext = servletContext;

    final var configuredScheme = mynaProperties.getBaseUri().getScheme();
    this.scheme = configuredScheme.equals("") ? "http" : configuredScheme;
    if (!this.scheme.equals("http") && !this.scheme.equals("https")) {
      throw new IllegalArgumentException(
          "scheme for base url should be http or https, see myna.base-url.scheme option"
      );
    }

    final var envAddress = env.getProperty("server.address");
    servletHost = (envAddress != null) ? envAddress : "localhost";

    final var configuredHost = mynaProperties.getBaseUri().getHost();
    if (configuredHost != null && !configuredHost.equals("")) {
      host = configuredHost;
    } else {
      host = servletHost;
    }

    final var configuredPort = mynaProperties.getBaseUri().getPort();
    if (configuredPort != null) {
      port = configuredPort;
    } else {
      updatePortFromServletPort = true;
    }
  }

  @EventListener
  public void serverInitEventHandler(final ServletWebServerInitializedEvent event) {
    final var bindPort = event.getWebServer().getPort();
    if (bindPort != -1) {
      servletPort = bindPort;
      if (updatePortFromServletPort) {
        port = bindPort;
      }
    }
  }

  public String getScheme() {
    return this.scheme;
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public int getServletPort() {
    return servletPort;
  }

  @Nullable
  public Integer getUriPort() {
    final var scheme = getScheme();
    if (port != null) {
      if ((scheme.equals("http") && port.equals(80)) || (scheme.equals("https") && port.equals(443))) {
        return null;
      }
      return port;
    }
    return null;
  }

  public String getContextPath() {
    return servletContext.getContextPath();
  }

  public UriComponentsBuilder getBaseUriBuilder() {
    final var builder = UriComponentsBuilder.newInstance().scheme(getScheme()).host(getHost());
    final var uriPort = getUriPort();
    if (uriPort != null) {
      builder.port(uriPort);
    }
    if (!getContextPath().equals("")) {
      return builder.path(getContextPath());
    }
    return builder;
  }

  public UriComponentsBuilder getBaseRelativeUriBuilder() {
    final var builder = UriComponentsBuilder.newInstance();
    if (!getContextPath().equals("")) {
      return builder.path(getContextPath());
    }
    return builder;
  }

  public String getBaseUri() {
    return getBaseUriBuilder().toUriString();
  }
}
