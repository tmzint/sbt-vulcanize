lazy val root = (project in file(".")).settings(
    pipelineStages := Seq(vulcanize)
).enablePlugins(SbtWeb)
