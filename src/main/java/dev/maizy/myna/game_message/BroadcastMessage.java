package dev.maizy.myna.game_message;
/*
 * Copyright (c) Nikita Kovalev, maizy.dev, 2024
 * See LICENSE.txt for details.
 */

public record BroadcastMessage(Message message, Long exceptWsId) {
}
