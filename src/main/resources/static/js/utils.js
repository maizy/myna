"use strict";

const debugEnabled = window.location.hash.indexOf('debug') !== -1;

export function debug() {
    if (debugEnabled && window.console !== undefined && window.console.debug !== undefined) {
        window.console.debug.apply(null, arguments);
    }
}

export function error() {
    if (window.console !== undefined && window.console.error !== undefined) {
        window.console.error.apply(null, arguments);
    }
}

export function randString(length) {
    let result = '';
    const alphabet = 'abcdefghijklmnopqrstuvwxyz0123456789';
    let counter = 0;
    while (counter < length) {
      result += alphabet.charAt(Math.floor(Math.random() * alphabet.length));
      counter += 1;
    }
    return result;
}
