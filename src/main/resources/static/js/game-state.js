(function () {
    window.Myna = window.Myna || {};

    function GameState(gameUriTemplate, currentGameState, messageBus) {
        this.gameUriTemplate = gameUriTemplate;
        this.messageBus = messageBus;
        this.currentGameState = currentGameState;

        this._pagesByState = {
            upcomming: 'lobby',
            launched: 'playground',
            finished: 'credits',
        };
    }

    GameState.prototype.init = function () {
        this.messageBus.addEventListiner('game_state_changed', this.gameStateChanged.bind(this));
    };

    GameState.prototype.gameStateChanged = function (event) {
        if (event.newState !== this.currentGameState) {
            const page = this._pagesByState[event.newState];
            if (page !== undefined) {
                document.location = this.gameUriTemplate.replace('PAGE', page);
            }
        }
    };

    window.Myna.GameState = GameState;
})();
