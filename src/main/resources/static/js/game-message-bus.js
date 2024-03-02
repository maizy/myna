"use strict";

import * as utils from "./utils.js";

export class GameMessageBus {
    constructor(gameId, myPlayerId) {
        this.gameId = gameId;
        this.myPlayerId = myPlayerId;
        this._connection = null;
        this._messageListeners = {
            event: {},
            response: {}
        };
        this._requests = {};
    }

    init() {
        this._connect();
    };

    _connect() {
        const wsProto = (document.location.protocol === "http:") ? "ws:" : "wss:";
        let wsUri = `${wsProto}//${document.location.host}/ws/game?gameId=${encodeURIComponent(this.gameId)}`;
        if (this.myPlayerId !== null) {
            wsUri += `&rulesetPlayerId=${this.myPlayerId}`;
        }

        this._connection = new WebSocket(wsUri);

        this._connection.addEventListener("open", (event) => utils.debug("ws connection opened", wsUri, event));
        this._connection.addEventListener("error", (event) => utils.debug("ws connection error", event));
        this._connection.addEventListener("close", (event) => {
            utils.debug("ws connection closed", event);
            this._connection = null;
        });
        this._connection.addEventListener("message", (event) => {
            let message;
            try {
                message = JSON.parse(event.data);
            } catch (e) {
                utils.error("unable to parse game message", `body=${event.data}`, e);
                return;
            }
            this._onMessageReceived(message);
        });
    }

    _onMessageReceived(message) {
        utils.debug("game message received", message);
        let callbacks;
        if (message.kind === 'event') {
            callbacks = this._messageListeners.event[message.eventType];
        } else if (message.kind === 'response') {
            callbacks = this._messageListeners.response[message.responseType];
            const oid = message.oid;
            if (oid && this._requests[oid] !== undefined) {
                const requestCallback = this._requests[oid];
                delete this._requests[oid];
                requestCallback.call(this, message);
            }
        } else {
            utils.error(`Unknown message kind: ${message.kind}`);
        }
        if (callbacks) {
            for (let callback of callbacks) {
                callback.call(this, message);
            }
        }
    }

    send(message) {
        if (this._connection !== null) {
            utils.debug("send game message", message);
            const messageJson = JSON.stringify(message);
            this._connection.send(messageJson);
        } else {
            utils.error("unable to send game message without ws connection");
        }
    }

    request(message, callback) {
        if (message.kind !== 'request') {
            utils.error("Wrong message kind for request", message);
            return;
        }
        const oid = `${+new Date()}-${utils.randString(6)}`;
        message.oid = oid;
        this._requests[oid] = callback;
        this.send(message);
    }

    addGameEventListiner(eventType, callback) {
        this._messageListeners.event[eventType] = this._messageListeners.event[eventType] || [];
        this._messageListeners.event[eventType].push(callback);
    }

    addResponseListiner(responseType, callback) {
        this._messageListeners.response[responseType] = this._messageListeners.response[responseType] || [];
        this._messageListeners.response[responseType].push(callback);
    }
}
