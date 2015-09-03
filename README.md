sbt-vulcanize
========

Allows the usage of vulcanize from within sbt.

Use the addSbtPlugin in your project's plugins.sbt:

```scala
addSbtPlugin("com.tmzint.sbt" % "sbt-vulcanize" % "0.2.0")
```

Your project's build file also needs to enable sbt-web plugins. For example with build.sbt:

```scala
lazy val root = (project in file(".")).enablePlugins(SbtWeb)
```

```scala
pipelineStages := Seq(vulcanize)
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


By default `main.html` is the entry point fed to Vulcanize. Beyond just `main.html`, you can use an expression in your `build.sbt` like the
following, which uses all html files not starting with an `_` as entry points:

```scala
includeFilter in vulcanize := GlobFilter("*.html")

excludeFilter in vulcanize := (GlobFilter("_*.html")
```
