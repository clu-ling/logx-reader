name := "labrador-core"

libraryDependencies ++= {

  val procVersion   = "7.4.4"

  Seq(
    "org.clulab"    %% "processors-main"          % procVersion,
    "org.scalatest" %% "scalatest" % "3.0.5"
  )
}
