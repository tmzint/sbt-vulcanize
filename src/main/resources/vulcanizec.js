(function () {

    "use strict";

    // Ensure we have a promise implementation.
    if (typeof Promise === 'undefined') {
        var Promise = require("es6-promise").Promise;
        global.Promise = Promise;
    }

    var args = process.argv;
    var os = require("os");
    var fs = require("fs");
    var vulcanize = require("vulcanize");
    var path = require("path");

})();
