"use strict";

export class GameState {
    constructor(gameUriTemplate, currentGameState, messageBus) {
        this.gameUriTemplate = gameUriTemplate;
        this.messageBus = messageBus;
        this.currentGameState = currentGameState;

        this._pagesByState = {
            upcomming: 'lobby',
            launched: 'playground',
            finished: 'credits',
        };
    }

    init() {
        this.messageBus.addGameEventListiner('game_state_changed', this.gameStateChanged.bind(this));
    }

    gameStateChanged(event) {
        if (event.newState !== this.currentGameState) {
            const page = this._pagesByState[event.newState];
            if (page !== undefined) {
                document.location = this.gameUriTemplate.replace('PAGE', page);
            }
        }
    }
}
