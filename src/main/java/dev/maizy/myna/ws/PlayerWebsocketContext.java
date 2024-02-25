package dev.maizy.myna.ws;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */


import dev.maizy.myna.ruleset.Player;
import java.util.Optional;

public record PlayerWebsocketContext(long wsId, String gameId, String uid, Optional<Player> rulesetPlayer) {
}
