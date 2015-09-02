import com.typesafe.sbt.web.Import.WebKeys
import com.typesafe.sbt.web.SbtWeb
import SbtWeb.autoImport._
import WebKeys._
import sbt.ScriptedPlugin._

organization := "com.tmzint.sbt"
name := "sbt-vulcanize"
description := "sbt-web vulcanize plugin"

scalaVersion := "2.10.4"
sbtPlugin := true

libraryDependencies ++= Seq(
)

lazy val root = (project in file(".")).enablePlugins(SbtWeb)

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.1.2")

publishMavenStyle := false

publishTo := {
    if (isSnapshot.value) Some(Classpaths.sbtPluginSnapshots)
    else Some(Classpaths.sbtPluginReleases)
}

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }

scriptedBufferLog := false
