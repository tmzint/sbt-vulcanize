(function () {
    "use strict";

    var _args = process.argv;
    var os = require("os");
    var fs = require("fs");
    var vulcan = require("vulcanize");
    var mkdirp = require("mkdirp");
    var path = require("path");

    var SOURCE_FILE_MAPPINGS_ARG = 2;
    var TARGET_ARG = 3;
    var OPTIONS_ARG = 4;

    var sourceFileMappings = JSON.parse(_args[SOURCE_FILE_MAPPINGS_ARG]);
    var target = _args[TARGET_ARG];
    var optionsString = _args[OPTIONS_ARG];
    var options = JSON.parse(optionsString);

    sourceFileMappings.forEach(function (sourceFileMapping) {

        var input = sourceFileMapping[0];
        var outputFile = sourceFileMapping[1];
        var output = path.join(target, outputFile);

        var args = {};
        args.excludes = options.exclude;
        args.stripExcludes = options.stripExclude;
        args.stripComments = options.stripComments;
        args.implicitStrip = !options.noImplicitStrip;
        args.inlineScripts = options.inlineScripts;
        args.inlineCss = options.inlineCss;

        var writeOutput = function (content, onDone) {
            mkdirp(path.dirname(output), function (e) {
                if (e) throw e;
                fs.writeFile(output, content, "utf8", onDone);
            });
        };

        (new vulcan(args)).process(input, function(err, content) {

            if (err) {
                process.stderr.write(require('util').inspect(err));
                process.exit(1);
            }

            writeOutput(content, function (e) {
                if (e) throw e;
                console.log(input + " -> " + output);
            });

        });

    });
})();
