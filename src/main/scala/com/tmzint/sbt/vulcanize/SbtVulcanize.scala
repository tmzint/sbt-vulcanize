package com.tmzint.sbt.vulcanize

import sbt._
import sbt.Keys._
import com.typesafe.sbt.web._
import com.typesafe.sbt.jse.SbtJsTask
import spray.json._

object Import {

    object VulcanizeKeys {
        val vulcanize = TaskKey[Seq[File]]("vulcanize", "Invoke Vulcanize.")

        val inlineScripts = SettingKey[Boolean]("vulcanize-inline-scripts", "Inline external scripts.")
        val inlineCss = SettingKey[Boolean]("vulcanize-inline-css", "Inline external stylesheets.")
        val exclude = SettingKey[Seq[String]]("vulcanize-exclude", "Exclude subpaths from root.")
        val stripExclude = SettingKey[String]("vulcanize-strip-exclude", "Exclude a subpath and strip the link that includes it.")
        val stripComments = SettingKey[Boolean]("vulcanize-strip-comments", "Strips all HTML comments not containing an @license from the document.")
        val noImplicitStrip = SettingKey[Boolean]("vulcanize-no-implicit-strip", "DANGEROUS! Avoid stripping imports of the transitive dependencies of imports specified with `--exclude`. May result in duplicate javascript inlining.")
        val abspath = SettingKey[Boolean]("vulcanize-abspath", "Make all adjusted urls absolute.")
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
    import SbtJsTask.autoImport.JsTaskKeys._
    import autoImport.VulcanizeKeys._

    //TODO: depend on web-assets:jseNpmNodeModules

    val vulcanizeUnscopedSettings = Seq(

        //includeFilter := GlobFilter("main.less"),

        jsOptions := JsObject(
            "inlineScripts" -> JsBoolean(inlineScripts.value),
            "inlineCss" -> JsBoolean(inlineCss.value),
            "exclude" -> JsArray(exclude.value.map(JsString(_)).toVector),
            "stripExclude" -> JsString(stripExclude.value),
            "stripComments" -> JsBoolean(stripComments.value),
            "noImplicitStrip" -> JsBoolean(noImplicitStrip.value),
            "abspath" -> JsBoolean(abspath.value)
        ).toString()
    )

    override def projectSettings = Seq(
        inlineScripts := false,
        inlineCss := false,
        exclude := Seq.empty,
        stripExclude := "",
        stripComments := false,
        noImplicitStrip := false,
        abspath := false
    ) ++ inTask(vulcanize)(
        SbtJsTask.jsTaskSpecificUnscopedSettings ++
            inConfig(Assets)(vulcanizeUnscopedSettings) ++
            inConfig(TestAssets)(vulcanizeUnscopedSettings) ++
            Seq(
                moduleName := "vulcanize",
                shellFile := getClass.getClassLoader.getResource("vulcanizec.js"),

                taskMessage in Assets := "vulcanization",
                taskMessage in TestAssets := "test vulcanization"
            )
    ) ++ SbtJsTask.addJsSourceFileTasks(vulcanize) ++ Seq(
        vulcanize in Assets := (vulcanize in Assets).dependsOn(webModules in Assets).value,
        vulcanize in TestAssets := (vulcanize in TestAssets).dependsOn(webModules in TestAssets).value
    )
}
