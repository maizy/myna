package dev.maizy.myna.integration_test.container;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.utility.DockerImageName;

public final class Redis {

  public static final String version = "7.2.3-alpine";
  public static final String redisPass = "testpass";

  public static RedisContainer build() {
    return new RedisContainer(DockerImageName.parse("redis:" + version))
          .withExposedPorts(6379)
          .withCommand("redis-server", "--requirepass", redisPass);
  }

  public static void use(RedisContainer instance, DynamicPropertyRegistry registry) {
    registry.add("spring.redis.host", instance::getHost);
    registry.add("spring.redis.port", () -> instance.getMappedPort(6379).toString());
    registry.add("spring.redis.password", () -> redisPass);
  }

  private Redis() {
  }
}
