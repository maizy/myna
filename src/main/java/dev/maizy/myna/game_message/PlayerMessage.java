package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

import dev.maizy.myna.ws.PlayerWebsocketContext;

public record PlayerMessage(PlayerWebsocketContext context, Message message) {
}
