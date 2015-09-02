import _root_.sbtrelease.ReleasePlugin.ReleaseKeys
import _root_.sbtrelease.ReleasePlugin.ReleaseKeys._
import _root_.sbtrelease.ReleasePlugin._
import _root_.sbtrelease.ReleaseStateTransformations
import _root_.sbtrelease.ReleaseStateTransformations._
import _root_.sbtrelease.ReleaseStep
import _root_.sbtrelease._
import sbt.ScriptedPlugin._
import sbtrelease._
import ReleaseStateTransformations._
import ReleaseKeys._

releaseSettings

publishArtifactsAction := PgpKeys.publishSigned.value

tagName := (version in ThisBuild).value

lazy val scriptedKey = taskKey[Unit]("scripted")

scriptedKey := {
    val log = streams.value.log
    log.info("Executing scripted...")
    val _ = scripted.toTask("").value
    log.info("...scripted Done!")
}

lazy val runScripted: ReleaseStep = releaseTask(scriptedKey in ThisProject)

releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    runScripted,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion/*,
    pushChanges*/
)
