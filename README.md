sbt-vulcanize
========

Allows the usage of vulcanize from within sbt.

Needs vulcanize and mkdirp installed in node path:

 ```js
 {
    ...
   "dependencies": {
     "vulcanize": "^1.14.0",
     "mkdirp": "^0.5.0"
   },
    ...
 }
 ```

Use the addSbtPlugin in your project's plugins.sbt:

```scala
addSbtPlugin("com.tmzint.sbt" % "sbt-vulcanize" % "0.4.0")
```

Your project's build file also needs to enable sbt-web plugins. For example with build.sbt:

```scala
lazy val root = (project in file(".")).enablePlugins(SbtWeb)
```

Enabling the Vulcanize pipline stage:

```scala
pipelineStages := Seq(vulcanize/*, gzip*/)
```

The plugin allows the use of all options of the vulcanize cli https://github.com/polymer/vulcanize - at point of writing.

Option              | Description
--------------------|------------
inlineScripts       | Inline external scripts.
inlineCss           | Inline external stylesheets.
exclude             | Exclude subpaths from root.
stripExclude        | Exclude a subpath and strip the link that includes it.
stripComments       | Strips all HTML comments not containing an @license from the document.
noImplicitStrip     | DANGEROUS! Avoid stripping imports of the transitive dependencies of imports specified with `--exclude`. May result in duplicate javascript inlining.
abspath             | Make all adjusted urls absolute.
addImport           | Add these imports to the target HTML before vulcanizing.
redirect            | Takes an argument in the form of URI &#124; PATH where url is a URI composed of a protocol, hostname, and path and PATH is a local filesystem path to replace the matched URI part with. Multiple redirects may be specified; the earliest ones have the highest priority.


By default `main.html` is the entry point fed to Vulcanize. Beyond just `main.html`, you can use an expression in your `build.sbt` like the
following, which uses all html files not starting with an `_` as entry points:

```scala
includeFilter in vulcanize := GlobFilter("*.html")

excludeFilter in vulcanize := GlobFilter("_*.html")
```
