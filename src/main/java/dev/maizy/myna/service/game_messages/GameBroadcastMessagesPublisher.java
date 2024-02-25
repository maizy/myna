package dev.maizy.myna.service.game_messages;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.maizy.myna.game_message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameBroadcastMessagesPublisher {

  private static final Logger log = LoggerFactory.getLogger(GameBroadcastMessagesPublisher.class);

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public GameBroadcastMessagesPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  public void publishGameMessage(String gameId, Long exceptWsId, Message message) {
    final var redisMessage = ImmutableRedisBusMessage.builder().exceptWsId(exceptWsId).message(message).build();
    final String messageBody;
    try {
      messageBody = objectMapper.writeValueAsString(redisMessage);
    } catch (JsonProcessingException e) {
      log.error("Unable to serialize game message, gameId={}: {}", gameId, e.getOriginalMessage());
      return;
    }
    redisTemplate.convertAndSend(GameTopic.forGame(gameId).getTopic(), messageBody);
  }
}
