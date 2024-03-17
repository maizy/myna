package dev.maizy.myna.game_model;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGameModel.class)
@JsonDeserialize(as = ImmutableGameModel.class)
public abstract class GameModel {

  public abstract Zone gameZone();
  public abstract List<GameObject> objects();
}
