package com.tmzint.sbt.vulcanize

import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys._
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt._
import sbt.Keys._
import com.typesafe.sbt.web._
import com.typesafe.sbt.jse.{SbtJsEngine, SbtJsTask}
import spray.json._

object Import {

    val vulcanize = TaskKey[Pipeline.Stage]("vulcanize", "Perform vulcanize on the asset pipeline.")

    object VulcanizeKeys {
        val inlineScripts = SettingKey[Boolean]("vulcanize-inline-scripts", "Inline external scripts.")
        val inlineCss = SettingKey[Boolean]("vulcanize-inline-css", "Inline external stylesheets.")
        val exclude = SettingKey[Seq[String]]("vulcanize-exclude", "Exclude subpaths from root.")
        val stripExclude = SettingKey[Seq[String]]("vulcanize-strip-exclude", "Exclude a subpath and strip the link that includes it.")
        val stripComments = SettingKey[Boolean]("vulcanize-strip-comments", "Strips all HTML comments not containing an @license from the document.")
        val noImplicitStrip = SettingKey[Boolean]("vulcanize-no-implicit-strip", "DANGEROUS! Avoid stripping imports of the transitive dependencies of imports specified with `--exclude`. May result in duplicate javascript inlining.")
        val abspath = SettingKey[Boolean]("vulcanize-abspath", "Make all adjusted urls absolute.")
        val addImport = SettingKey[Seq[String]]("vulcanize-add-import", "Add these imports to the target HTML before vulcanizing.")
        val redirect = SettingKey[Seq[String]]("vulcanize-redirect", "Takes arguments in the form of URI | PATH where url is a URI composed of a protocol, hostname, and path and PATH is a local filesystem path to replace the matched URI part with. Multiple redirects may be specified; the earliest ones have the highest priority.")
    }

}

/**
 * Created by timo merlin zint on 02.09.15.
 */
object SbtVulcanize extends AutoPlugin {

    override def requires = SbtJsTask

    override def trigger = AllRequirements

    val autoImport = Import

    import SbtWeb.autoImport._
    import WebKeys._
    import SbtJsEngine.autoImport.JsEngineKeys._
    import SbtJsTask.autoImport.JsTaskKeys._
    import autoImport._
    import VulcanizeKeys._

    val vulcanizeUnscopedSettings = Seq(
        jsOptions := JsObject(
            "inlineScripts" -> JsBoolean(inlineScripts.value),
            "inlineCss" -> JsBoolean(inlineCss.value),
            "exclude" -> JsArray(exclude.value.map(JsString(_)).toVector),
            "stripExclude" -> JsArray(stripExclude.value.map(JsString(_)).toVector),
            "stripComments" -> JsBoolean(stripComments.value),
            "noImplicitStrip" -> JsBoolean(noImplicitStrip.value),
            "abspath" -> JsBoolean(abspath.value),
            "addImport" -> JsArray(addImport.value.map(JsString(_)).toVector),
            "redirect" -> JsArray(redirect.value.map(JsString(_)).toVector)
        ).toString()
    )

    override def projectSettings = Seq(
        includeFilter in vulcanize := "main.html",
        excludeFilter in vulcanize := HiddenFileFilter,
        resourceManaged in vulcanize := webTarget.value / vulcanize.key.label,
        inlineScripts := false,
        inlineCss := false,
        exclude := Seq.empty,
        stripExclude := Seq.empty,
        stripComments := false,
        noImplicitStrip := false,
        abspath := false,
        addImport := Seq.empty,
        redirect := Seq.empty,
        vulcanize := runVulcanize.dependsOn(webJarsNodeModules in Plugin).value
    ) ++ inTask(vulcanize)(vulcanizeUnscopedSettings) ++ Seq(
        vulcanize in Assets := (vulcanize in Assets).dependsOn(webModules in Assets).value,
        vulcanize in TestAssets := (vulcanize in TestAssets).dependsOn(webModules in TestAssets).value
    )

    private def runVulcanize: Def.Initialize[Task[Pipeline.Stage]] = Def.task {
        mappings =>
            val inc = (includeFilter in vulcanize).value
            val exc = (excludeFilter in vulcanize).value
            val mappingsFiltered = mappings
                .filter(f => !f._1.isDirectory && inc.accept(f._1) && !exc.accept(f._1))
            SbtWeb.syncMappings(
                streams.value.cacheDirectory,
                mappingsFiltered,
                (resourceManaged in vulcanize).value
            )

            val jsRes = getClass.getClassLoader.getResource("vulcanizec.js")

            val js = SbtWeb.copyResourceTo(
                (target in Plugin).value / moduleName.value,
                jsRes,
                streams.value.cacheDirectory
            )

            val cacheDirectory = streams.value.cacheDirectory / vulcanize.key.label
            val runUpdate = FileFunction.cached(cacheDirectory, FilesInfo.hash) { _ =>
                mappingsFiltered.foreach( m => {
                    streams.value.log.info("Vulcanizing")
                    SbtJsTask.executeJs(
                        state.value,
                        (engineType in vulcanize).value,
                        (command in vulcanize).value,
                        Seq.empty,
                        js,
                        Seq(m._1.getPath, m._2, (resourceManaged in vulcanize).value.getPath, (jsOptions in vulcanize).value),
                        (timeoutPerSource in vulcanize).value * mappingsFiltered.size
                    )
                })
                (resourceManaged in vulcanize).value.***.get.toSet
            }

            val resMappings = runUpdate((resourceManaged in vulcanize).value.***.get.toSet)
                .filter(_.isFile).pair(relativeTo((resourceManaged in vulcanize).value))
            (mappings.toSet -- mappingsFiltered.toSet ++ resMappings).toSeq

    }
}
