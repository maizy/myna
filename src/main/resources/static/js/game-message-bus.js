(function () {
    window.Myna = window.Myna || {};

    const Utils = Myna.Utils;

    function GameMessageBus(gameId, myPlayerId) {
        this.gameId = gameId;
        this.myPlayerId = myPlayerId;
        this._connection = null;
        this._messageListeners = {
            event: {},
            response: {}
        };
        this._requests = {};
    }

    GameMessageBus.prototype.init = function () {
        this._connect();
    };

    GameMessageBus.prototype._connect = function () {
        const wsProto = (document.location.protocol === "http:") ? "ws:" : "wss:";
        let wsUri = `${wsProto}//${document.location.host}/ws/game?gameId=${encodeURIComponent(this.gameId)}`;
        if (this.myPlayerId !== null) {
            wsUri += `&rulesetPlayerId=${this.myPlayerId}`;
        }

        this._connection = new WebSocket(wsUri);

        this._connection.addEventListener("open", (event) => Utils.debug("ws connection opened", wsUri, event));
        this._connection.addEventListener("error", (event) => Utils.debug("ws connection error", event));
        this._connection.addEventListener("close", (event) => {
            Utils.debug("ws connection closed", event);
            this._connection = null;
        });
        this._connection.addEventListener("message", (event) => {
            let message;
            try {
                message = JSON.parse(event.data);
            } catch (e) {
                Utils.error("unable to parse game message", `body=${event.data}`, e);
                return;
            }
            this._onMessageReceived(message);
        });
    };

    GameMessageBus.prototype._onMessageReceived = function (message) {
        Utils.debug("game message received", message);
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
            Utils.error(`Unknown message kind: ${message.kind}`);
        }
        if (callbacks) {
            for (let callback of callbacks) {
                callback.call(this, message);
            }
        }
    };

    GameMessageBus.prototype.send = function (message) {
        if (this._connection !== null) {
            Utils.debug("send game message", message);
            const messageJson = JSON.stringify(message);
            this._connection.send(messageJson);
        } else {
            Utils.error("unable to send game message without ws connection");
        }
    };

    GameMessageBus.prototype.request = function (message, callback) {
        if (message.kind !== 'request') {
            Utils.error("Wrong message kind for request", message);
            return;
        }
        const oid = `${+new Date()}-${Utils.randString(6)}`;
        message.oid = oid;
        this._requests[oid] = callback;
        this.send(message);
    };

    GameMessageBus.prototype.addEventListiner = function (eventType, callback) {
        this._messageListeners.event[eventType] = this._messageListeners.event[eventType] || [];
        this._messageListeners.event[eventType].push(callback);
    };

    GameMessageBus.prototype.addResponseListiner = function (responseType, callback) {
        this._messageListeners.response[responseType] = this._messageListeners.response[responseType] || [];
        this._messageListeners.response[responseType].push(callback);
    };

    window.Myna.GameMessageBus = GameMessageBus;
})();
