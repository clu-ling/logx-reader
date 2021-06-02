name := "logx-reader"

resolvers += "Artifactory" at "http://artifactory.cs.arizona.edu:8081/artifactory/sbt-release"

libraryDependencies ++= {
  //val procVersion   = "8.0.3"
  val procVersion   = "8.1.0"
  val json4sVersion = "3.2.11" // Spark is incompatible with newer versions: https://github.com/json4s/json4s/issues/316  "3.5.2"
  val luceneVersion = "6.6.0"
  val http4sVersion = "0.20.22"

  Seq(
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5",
    //"org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "com.typesafe.scala-logging" %%  "scala-logging" % "3.5.0",
    "ch.qos.logback" %  "logback-classic" % "1.1.7",
    "org.clulab"    %% "processors-main"          % procVersion,
    "org.clulab"    %% "processors-corenlp"       % procVersion,
    "org.clulab"    %% "processors-odin"          % procVersion,
    "org.clulab"    %% "processors-openie"        % procVersion,
    // "org.clulab"    %% "processors-modelsmain"    % procVersion,
    // "org.clulab"    %% "processors-modelscorenlp" % procVersion,
    "org.json4s" %% "json4s-core" % json4sVersion,
    "org.json4s" %% "json4s-jackson" % json4sVersion,
    "org.yaml"      %  "snakeyaml"               % "1.14",
    // lucene
    "org.apache.lucene" % "lucene-core" % luceneVersion,
    "org.apache.lucene" % "lucene-queryparser" % luceneVersion,
    // http4s
    "org.http4s" %% "http4s-json4s-jackson" % http4sVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion
  )
}