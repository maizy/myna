"use strict";

import * as utils from "./utils.js";

export class Surface {
    constructor(messageBus, containerId) {
        this.messageBus = messageBus;
        this.containerId = containerId || "game-surface";
        this._svg = null;
        this._$messageLoading = null;
        this._$messageError = null;

        this._byItemId = {};
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

        this.messageBus.addGameEventListiner('object_move', event => this.moveObject(event.itemId, event.position));
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
        this._byItemId = {};
        svg.addTo(`#${this.containerId}`).size(gameZone.size.width, gameZone.size.height);
        const gameZoneGroup = svg.nested();
        await this._drawItem(gameZoneGroup, {size: gameZone.size}, gameZone.appereance);
        this._byItemId[gameZone.itemId] = gameZoneGroup;
        if (gameModel.objects) {
            const sortedObjects = [...gameModel.objects];
            sortedObjects.sort((a, b) => a.zIndex - b.zIndex);
            for (let object of sortedObjects) {
                await this._addGameObject(svg, object);
            }
        }
        this._$messageLoading.classList.add("template");
    }

    moveObject(itemId, position) {
        const objectGroup = this._byItemId[itemId];
        if (objectGroup !== undefined) {
            objectGroup.move(position.x, position.y);
        } else {
            utils.error('try to move unknown object', itemId);
        }
    }

    async _addGameObject(svg, object) {
        const objectGroup = svg.nested();
        await this._drawItem(objectGroup, object.currentPosition, object.currentAppereance);
        objectGroup.css('cursor', 'pointer');
        this._byItemId[object.itemId] = objectGroup;
        if (object.isDraggable) {
            objectGroup.draggable();
            const dragzoneId = object.draggableZone ? object.draggableZone.itemId : 'root';
            objectGroup.on('dragmove.namespace', (e) => this._dragObject(object, dragzoneId, e));
            objectGroup.on('dragstart.namespace', (e) => this._dragStart(object, objectGroup, e));
            objectGroup.on('dragend.namespace', (e) => this._dragEnd(object, objectGroup, e));
        }
    }

    _dragStart(object, objectGroup, e) {
        this.messageBus.notify({
            eventType: 'object_drag_start',
            itemId: object.itemId,
            rulesetObjectId: object.rulesetObjectId
        });
    }

    _dragEnd(object, objectGroup, e) {
        this.messageBus.notify({
            eventType: 'object_drag_end',
            itemId: object.itemId,
            rulesetObjectId: object.rulesetObjectId,
            position: {x: objectGroup.x(), y: objectGroup.y()}
        });
    }

    _dragObject(object, dragzoneId, e) {
        const draggableZone = this._byItemId[dragzoneId];
        e.preventDefault();
        if (!draggableZone) {
            return;
        }
        const {handler, box: diff} = e.detail;
        const constraints = draggableZone.bbox();
        const el = handler.el;

        const {w, h} = el.bbox(); // only width & height are relevant for bbox here
        const x = el.x(),
              y = el.y();

        let newX = x + diff.x;
        let newY = y + diff.y;
        const newX2 = x + w + diff.x;
        const newY2 = y + h + diff.y;

        // top left constraint
        if (newX < constraints.x) {
            newX = constraints.x;
        }
        if (newY < constraints.y) {
            newY = constraints.y;
        }

        // bottom right constraint
        if (newX2 > constraints.x2) {
            newX = constraints.x2 - w;
        }
        if (newY2 > constraints.y2) {
            newY = constraints.y2 - h;
        }
        handler.move(newX - x, newY - y);

        const roundNewX = Math.round(newX);
        const roundNewY = Math.round(newY);
        if (roundNewX !== Math.round(x) || roundNewY !== Math.round(y)) {
            this.messageBus.notify({
                eventType: 'object_drag',
                itemId: object.itemId,
                rulesetObjectId: object.rulesetObjectId,
                position: {x: roundNewX, y: roundNewY}
            });
        }
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
