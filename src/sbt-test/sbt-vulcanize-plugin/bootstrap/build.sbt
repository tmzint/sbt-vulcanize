lazy val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(vulcanize)

//includeFilter in vulcanize := "main.html"

//val checkFileContents = taskKey[Unit]("Check for emptiness")
//
//checkFileContents := {
//    val contents = IO.read((WebKeys.public in Assets).value / "css" / "empty.css")
//
//    if (contents.nonEmpty) {
//        sys.error(s"Output should be empty, but got '$contents'")
//    }
//}
