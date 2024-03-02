"use strict";

export class Players {
    constructor(myPlayerId, messageBus) {
        this.myPlayerId = myPlayerId;
        this.messageBus = messageBus;
    }

    init() {
        this.messageBus.addGameEventListiner('players_state_changed', this.updatePlayersState.bind(this));
    }

    updatePlayersState(state) {
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
    }

    _$statusElement(playerId) {
        return window.document.getElementById(`player-status__${playerId}`);
    }

    _setPlaying(playerId) {
        const $elem = this._$statusElement(playerId);
        if ($elem) {
            $elem.className = 'bi bi-record-fill';
            $elem.style.color = '#64a338';
        }
    }

    _setAbsent(playerId) {
        const $elem = this._$statusElement(playerId);
        if ($elem) {
            $elem.className = 'bi bi-wifi-off';
            $elem.style.color = '#e03b24';
        }
    }
}
