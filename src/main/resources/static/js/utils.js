(function () {
    window.Myna = window.Myna || {};

    const utils = {
        debug: function () {
            if (window.console !== undefined && window.console.debug !== undefined) {
                window.console.debug.apply(null, arguments);
            }
        },
        error: function () {
            if (window.console !== undefined && window.console.error !== undefined) {
                window.console.error.apply(null, arguments);
            }
        },
        randString: function (length) {
            let result = '';
            const alphabet = 'abcdefghijklmnopqrstuvwxyz0123456789';
            let counter = 0;
            while (counter < length) {
              result += alphabet.charAt(Math.floor(Math.random() * alphabet.length));
              counter += 1;
            }
            return result;
        }
    };

    window.Myna.Utils = utils;
})();
