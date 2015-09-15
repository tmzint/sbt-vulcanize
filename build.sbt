import com.typesafe.sbt.web.Import.WebKeys
import com.typesafe.sbt.web.SbtWeb
import SbtWeb.autoImport._
import WebKeys._
import sbt.ScriptedPlugin._
import bintray.Keys._

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.1.2")

lazy val commonSettings = Seq(
    scalaVersion := "2.10.4",
    version in ThisBuild := "0.3.0",
    organization in ThisBuild := "com.tmzint.sbt"
)

lazy val root = (project in file(".")).
    settings(commonSettings ++ bintrayPublishSettings: _*).
    settings(
        sbtPlugin := true,
        name := "sbt-vulcanize",
        description := "sbt-web vulcanize plugin",
        licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
        publishMavenStyle := false,
        repository in bintray := "sbt-plugins",
        bintrayOrganization in bintray := None
    ).enablePlugins(SbtWeb)
