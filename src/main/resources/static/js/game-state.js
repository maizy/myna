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
        this.messageBus.addGameEventListiner(
            'game_state_changed',
            (event) => this.gameStateChanged(event.newState)
        );
        this.messageBus.addEventListiner("connected", () => {
            this.messageBus.request(
                {requestType: "get_game_state"},
                (message) => this.gameStateChanged(message.currentState)
            );
        });
    }

    gameStateChanged(state) {
        if (state !== this.currentGameState) {
            const page = this._pagesByState[state];
            if (page !== undefined) {
                document.location = this.gameUriTemplate.replace('PAGE', page);
            }
        }
    }
}
