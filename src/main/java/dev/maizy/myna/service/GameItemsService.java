package dev.maizy.myna.service;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.maizy.myna.game_model.GameModel;
import dev.maizy.myna.game_model.GameObject;
import dev.maizy.myna.game_model.ImmutableGameModel;
import dev.maizy.myna.game_model.ImmutableGameObject;
import dev.maizy.myna.game_model.ImmutableZone;
import dev.maizy.myna.game_model.Item;
import dev.maizy.myna.game_model.Ref;
import dev.maizy.myna.game_model.Zone;
import dev.maizy.myna.ruleset.Ruleset;
import dev.maizy.myna.surface.Point;
import dev.maizy.myna.surface.Rectangle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameItemsService {
  public static final String gameObjectsIdPrefix = "obj.";
  public static final String gameZoneId = "root";
  private static final Logger log = LoggerFactory.getLogger(GameItemsService.class);

  public static String gameObjectId(int index) {
    return gameObjectsIdPrefix + index;
  }

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public GameItemsService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  public GameModel initAndSaveGameModel(String gameId, Ruleset ruleset) {
    final var gameModelBuilder = ImmutableGameModel.builder();

    var zIndex = 0;
    final var gameZone = ImmutableZone.builder()
        .zIndex(zIndex++)
        .itemId(gameZoneId)
        .appereance(ruleset.gameZone().appearance())
        .build();
    gameModelBuilder.gameZone(gameZone);

    var objectIndex = 0;
    for (final var rulesetObj : ruleset.rootObjects().objects()) {
      final var currentState = rulesetObj.stateChangeStrategy().getStategyBuilder().apply(rulesetObj).nextState();
      final var gameObj = ImmutableGameObject.builder()
          .zIndex(zIndex++)
          .itemId(gameObjectId(objectIndex++))
          .isDraggable(true)
          .container(Ref.fromItem(gameZone))
          .currentAppereance(currentState.appearance())
          // TODO: positioning objects in ruleset
          .currentPosition(Rectangle.fromTopLeftAndSize(Point.zero(), rulesetObj.size()))
          .build();
      gameModelBuilder.addObject(gameObj);
    }
    final var gameModel = gameModelBuilder.build();
    saveGameModel(gameId, gameModel);
    return gameModel;
  }

  public void saveGameModel(String gameId, GameModel model) {
    final Map<String, String> items = new HashMap<>();

    try {
      items.put(model.gameZone().itemId(), objectMapper.writeValueAsString(model.gameZone()));
    } catch (JsonProcessingException e) {
      throw new GameItemsServiceErrors.GameItemsServiceException(gameId, "Unable to serialize game zone", e);
    }

    model.objects().forEach(obj -> {
      try {
        items.put(obj.itemId(), objectMapper.writeValueAsString(obj));
      } catch (JsonProcessingException e) {
        throw new GameItemsServiceErrors.GameItemsServiceException(gameId, "Unable to serialize game object", e);
      }
    });

    redisTemplate.opsForHash().putAll(RedisKeys.itemsState(gameId), items);
  }

  public void saveItem(String gameId, Item item) {
    final var itemId = item.itemId();
    final String itemJson;
    try {
      itemJson = objectMapper.writeValueAsString(item);
    } catch (JsonProcessingException e) {
      throw new GameItemsServiceErrors.GameItemsServiceException(
          gameId, "Unable to serialize item with id=" + itemId, e
      );
    }
    redisTemplate.opsForHash().put(RedisKeys.itemsState(gameId), itemId, itemJson);
  }

  private <T extends Item> Optional<T> loadItem(String gameId, String itemId, Class<T> klass) {
    if (redisTemplate.opsForHash().get(RedisKeys.itemsState(gameId), itemId) instanceof String value) {
      try {
        return Optional.of(objectMapper.readValue(value, klass));
      } catch (JsonProcessingException e) {
        throw new GameItemsServiceErrors.GameItemsServiceException(
            gameId, "Unable to deserialize item", e
        );
      }
    }
    return Optional.empty();
  }

  public Optional<GameObject> loadGameObject(String gameId, String itemId) {
    return loadItem(gameId, itemId, GameObject.class);
  }

  public Optional<Zone> loadGameZone(String gameId) {
    return loadItem(gameId, gameZoneId, Zone.class);
  }

  public Optional<GameModel> loadGameModel(String gameId) {
    final var itemsJson = redisTemplate.opsForHash().entries(RedisKeys.itemsState(gameId));
    if (itemsJson.isEmpty()) {
      return Optional.empty();
    }

    final var gameModelBuiler = ImmutableGameModel.builder();

    if (itemsJson.get(gameZoneId) instanceof String gameZoneJson) {
      try {
        gameModelBuiler.gameZone(objectMapper.readValue(gameZoneJson, Zone.class));
      } catch (JsonProcessingException e) {
        throw new GameItemsServiceErrors.GameItemsServiceException(
            gameId, "Unable to deserialize gameZone", e
        );
      }
    } else {
      return Optional.empty();
    }

    final var gameObjects = itemsJson.entrySet().stream()
        .filter(entry -> (entry.getKey() instanceof String key) && key.startsWith(gameObjectsIdPrefix))
        .flatMap(entity -> {
          if (entity.getValue() instanceof String value) {
            try {
              return Stream.of(objectMapper.readValue(value, GameObject.class));
            } catch (JsonProcessingException e) {
              log.warn(
                  "Skip unparsable item: gameId={}, itemId={}: {}",
                  gameId, entity.getKey(), e.getOriginalMessage()
              );
              return Stream.empty();
            }
          } else {
            return Stream.empty();
          }
        })
        .sorted(Comparator.comparingInt(GameObject::zIndex))
        .toArray(GameObject[]::new);
    gameModelBuiler.addObjects(gameObjects);
    return Optional.of(gameModelBuiler.build());
  }
}
