(function () {
    window.Myna = window.Myna || {};

    function Players(myPlayerId, messageBus) {
        this.myPlayerId = myPlayerId;
        this.messageBus = messageBus;
    }

    Players.prototype.init = function () {
        this.messageBus.addEventListiner('players_state_changed', this.updatePlayersState.bind(this));
    };

    Players.prototype.updatePlayersState = function (state) {
        for (let player of state.players) {
            if (player.player.id === this.myPlayerId) {
                continue;
            }
            if (player.status === 'absent') {
                this._setAbsent(player.player.id);
            } else if (player.status === 'playing') {
                this._setPlaying(player.player.id);
            }
        }
    };

    Players.prototype._$statusElement = function (playerId) {
        return window.document.getElementById(`player-status__${playerId}`);
    };

    Players.prototype._setPlaying = function (playerId) {
        const $elem = this._$statusElement(playerId);
        if ($elem) {
            $elem.className = 'bi bi-record-fill';
            $elem.style.color = '#64a338';
        }
    };

    Players.prototype._setAbsent = function (playerId) {
        const $elem = this._$statusElement(playerId);
        if ($elem) {
            $elem.className = 'bi bi-wifi-off';
            $elem.style.color = '#e03b24';
        }
    };

    window.Myna.Players = Players;
})();
