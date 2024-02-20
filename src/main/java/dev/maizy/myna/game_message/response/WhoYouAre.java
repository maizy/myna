package dev.maizy.myna.game_message.response;

/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.maizy.myna.game_message.Response;
import dev.maizy.myna.game_message.ResponseType;
import dev.maizy.myna.ruleset.Player;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableWhoYouAre.class)
@JsonDeserialize(as = ImmutableWhoYouAre.class)
@JsonTypeName("who_you_are")
public abstract class WhoYouAre implements Response {

  @Override
  public ResponseType responseType() {
    return ResponseType.who_you_are;
  }

  @Override
  public abstract String gameId();

  public abstract Optional<String> oid();

  public abstract Optional<Player> player();
}
