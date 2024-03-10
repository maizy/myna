"use strict";

import * as utils from "./utils.js";

export class Surface {
    constructor(messageBus, containerId) {
        this.messageBus = messageBus;
        this.containerId = containerId || "game-surface";
        this._svg = null;
        this._$messageLoading = null;
        this._$messageError = null;
    }

    init() {
        this._$messageError = window.document.getElementById("message-error");
        this._$messageLoading = window.document.getElementById("message-loading");

        this.messageBus.addEventListiner("connected", () => {
            this._$messageLoading.classList.remove("template");
            this._$messageError.classList.add("template");
            this.messageBus.request({requestType: "get_full_view"}, (message) => this.repaint(message.gameModel));
        });

        this.messageBus.addEventListiner("disconnected", () => {
            this._$messageError.innerText = "Connection lost.\nReconnecting ...";
            this._$messageError.classList.remove("template");
        });
    }

    clean() {
        if (this._svg !== null) {
            this._svg.remove();
            this._svg = null;
        }
    }

    async repaint(gameModel) {
        utils.debug("repaint full view", gameModel);
        this.clean();
        const gameZone = gameModel.gameZone;
        const svg = SVG();
        this._svg = svg;
        svg.addTo(`#${this.containerId}`).size(gameZone.size.width, gameZone.size.height);
        const gameZoneGroup = svg.nested();
        await this._drawItem(gameZoneGroup, {size: gameZone.size}, gameZone.appereance);
        if (gameModel.objects) {
            const sortedObjects = [...gameModel.objects];
            sortedObjects.sort((a, b) => a.zIndex - b.zIndex);
            for (let object of sortedObjects) {
                await this._addGameObject(svg, object);
            }
        }
        this._$messageLoading.classList.add("template");
    }

    async _addGameObject(svg, object) {
        const objectGroup = svg.nested();
        await this._drawItem(objectGroup, object.currentPosition, object.currentAppereance);
    }

    async _drawItem(itemSvg, position, appereance) {
        const rect = itemSvg.rect(position.size.width, position.size.height);
        if (position.topLeft) {
            itemSvg.move(position.topLeft.x, position.topLeft.y);
        }
        let backgroundSvg;
        if (appereance.backgroundSvg) {
            backgroundSvg = await fetch(appereance.backgroundSvg).then((r) => r.text());
        }

        if (appereance.backgroundColor && appereance.backgroundColor.hex) {
            rect.fill(`#${appereance.backgroundColor.hex}`);
        } else {
            rect.fill({opacity: 0.0});
        }
        if (backgroundSvg) {
            itemSvg.svg(backgroundSvg);
            const svg = itemSvg.last();
            if (svg) {
                svg.center(rect.cx(), rect.cy());
            }
        }
        if (appereance.backgroundImage) {
            const image = itemSvg.image(appereance.backgroundImage);
            image.on('load', () => image.center(rect.cx(), rect.cy()));
        }
        if (appereance.text !== undefined && appereance.text !== null) {
            const text = itemSvg.text(appereance.text);
            const font = {size: '1.5em'};
            if (appereance.textColor && appereance.textColor.hex) {
                font.fill = `#${appereance.textColor.hex}`;
            }
            text.font(font);
            text.center(rect.cx(), rect.cy());
        }
    }
}
