import sbtassembly.MergeStrategy
import com.typesafe.sbt.packager.docker.DockerChmodType


// use commit hash as the version
// enablePlugins(GitVersioning)
// git.uncommittedSignifier := Some("DIRTY") // with uncommitted changes?
// git.baseVersion := "0.1.0-SNAPSHOT"

lazy val commonScalacOptions = Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  // "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-encoding", "utf8"
)



routesGenerator := InjectedRoutesGenerator

lazy val commonSettings = Seq(
  organization := "org.parsertongue",
  scalaVersion := "2.12.10",
  // we want to use -Ywarn-unused-import most of the time
  scalacOptions ++= commonScalacOptions,
  scalacOptions += "-Ywarn-unused-import",
  // -Ywarn-unused-import is annoying in the console
  scalacOptions in (Compile, console) := commonScalacOptions,
  // show test duration
  testOptions in Test += Tests.Argument("-oD"),
  // avoid dep. conflict in assembly task for webapp
  excludeDependencies += "commons-logging" % "commons-logging",
  parallelExecution in Test := false
)

// example specifying credentials using ENV variables:
// AWS_ACCESS_KEY_ID="XXXXXX" AWS_SECRET_KEY="XXXXXX"

// lazy val s3Settings = {
//   val s3BucketUrl = "s3://maven.parsertongue.org/snapshots"
//   Seq(
//     //resolvers += "Parsertongue.org Snapshots" at s3BucketUrl,
//     publishMavenStyle := true,
//     publishTo := Some("Parsertongue.org Snapshots" at s3BucketUrl)
//   )
// }

lazy val sharedDeps = {
  libraryDependencies ++= {
    val odinsonVersion      = "0.3.0-SNAPSHOT"
    Seq(
      "ai.lum"        %% "common"               % "0.1.2",
      //"ai.lum"        %% "odinson-core"         % odinsonVersion
    )
  }
}

lazy val assemblySettings = Seq(
  // Trick to use a newer version of json4s with spark (see https://stackoverflow.com/a/49661115/1318989)
  assemblyShadeRules in assembly := Seq(
    ShadeRule.rename("org.json4s.**" -> "shaded_json4s.@1").inAll
  ),
  assemblyMergeStrategy in assembly := {
    case refOverrides if refOverrides.endsWith("reference-overrides.conf") => MergeStrategy.first
    case logback if logback.endsWith("logback.xml") => MergeStrategy.first
    case netty if netty.endsWith("io.netty.versions.properties") => MergeStrategy.first
    case "messages" => MergeStrategy.concat
    case PathList("META-INF", "terracotta", "public-api-types") => MergeStrategy.concat
    case PathList("play", "api", "libs", "ws", xs @ _*) => MergeStrategy.first
    case PathList("org", "apache", "lucene", "analysis", xs @ _ *) => MergeStrategy.first
    //case lucene if lucene.contains("StandardAnalyzer") => MergeStrategy.first
    // case server if server.endsWith("reference-overrides.conf")
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val buildInfoSettings = Seq(
  buildInfoPackage := "org.parsertongue.mr",
  buildInfoOptions += BuildInfoOption.BuildTime,
  buildInfoKeys := Seq[BuildInfoKey](
    name, version, scalaVersion, sbtVersion, libraryDependencies, scalacOptions,
    "gitCurrentBranch" -> { git.gitCurrentBranch.value },
    "gitHeadCommit" -> { git.gitHeadCommit.value.getOrElse("") },
    "gitHeadCommitDate" -> { git.gitHeadCommitDate.value.getOrElse("") },
    "gitUncommittedChanges" -> { git.gitUncommittedChanges.value }
  )
)

lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(sharedDeps)
  .settings(assemblySettings)
  //.settings(s3Settings)

lazy val reader = (project in file("reader"))
  .settings(commonSettings)
  .settings(sharedDeps)
  .settings(assemblySettings)
  //.settings(s3Settings)
  .aggregate(core)
  .dependsOn(core)
  .settings(
    test in assembly := {}
  )

val gitDockerTag = settingKey[String]("Git commit-based tag for docker")
ThisBuild / gitDockerTag := {
  val shortHash: String = git.gitHeadCommit.value.get.take(7)  
  val uncommittedChanges: Boolean = (git.gitUncommittedChanges).value
  s"""${shortHash}${if (uncommittedChanges) "-DIRTY" else ""}"""
}

lazy val packagerSettings = {
  Seq(
    // see https://www.scala-sbt.org/sbt-native-packager/formats/docker.html
    dockerUsername := Some("parsertongue"),
    dockerAliases ++= Seq(
      dockerAlias.value.withTag(Option("latest")),
      dockerAlias.value.withTag(Option(gitDockerTag.value)),
      // see https://github.com/sbt/sbt-native-packager/blob/master/src/main/scala/com/typesafe/sbt/packager/docker/DockerAlias.scala
      //"gitlab-registry.logx.cloud/team/bbn/logx-reader:latest"
      DockerAlias(registryHost = Some("gitlab-registry.logx.cloud/team"), username = Some("bbn"), name = "logx-reader", tag = Some("latest")),
      DockerAlias(registryHost = Some("gitlab-registry.logx.cloud/team"), username = Some("bbn"), name = "logx-reader", tag = Some(gitDockerTag.value))
    ),
    packageName in Docker := "logx-reader-rest-api",
    // "openjdk:11-jre-alpine"
    dockerBaseImage := "openjdk:11",
    //dockerRepository := Some("index.docker.io"),
    maintainer in Docker := "Gus Hahn-Powell <gus@parsertongue.org>",
    dockerExposedPorts in Docker := Seq(9000),
    //dockerChmodType := DockerChmodType.UserGroupWriteExecute
    javaOptions in Universal ++= Seq(
      "-J-Xmx2G",
      // avoid writing a PID file
      "-Dplay.server.pidfile.path=/dev/null"
    )
  )
}

lazy val webapp = (project in file("rest"))
  .enablePlugins(PlayScala)
  .enablePlugins(BuildInfoPlugin)
  //.enablePlugins(DockerPlugin)
  //.enablePlugins(sbtdocker.DockerPlugin)
  .aggregate(reader)
  .dependsOn(reader)
  .settings(commonSettings)
  .settings(packagerSettings)
  .settings(sharedDeps)
  .settings(buildInfoSettings)
  .settings(assemblySettings)
  .settings(
    mainClass in assembly := Some("play.core.server.ProdServerStart"),
    fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)
  )

// lazy val sparkTasks = (project in file("spark"))
//   .enablePlugins(BuildInfoPlugin)
//   .settings(buildInfoSettings)
//   .aggregate(reader)
//   .dependsOn(reader)
//   .settings(commonSettings)
//   .settings(assemblySettings)
//   .settings(
//     scalaVersion := "2.12.10",
//     //unmanagedResourceDirectories in Compile += { baseDirectory.value / "grammars" },
//     assemblyJarName in assembly := "logx-spark.jar"
//   )

lazy val web = taskKey[Unit]("Launches the webapp in dev mode.")
web := (run in Compile in webapp).toTask("").value

lazy val editGrammars = taskKey[Unit]("Copies and modifies grammars for reading via resources")


// FIXME: These settings are only picked up if they are left top-level
flatten in EditSource := false
// change pathPrefix in grammars to read from resources
substitutions in EditSource += sub("""pathPrefix: .*$""".r, "pathPrefix: \"org/parsertongue/reader/grammars/logx\"")

variables in EditSource += "actionFlow" -> "DOLLAR{actionFlow}"
variables in EditSource += "agents" -> "DOLLAR{agents}"
variables in EditSource += "causeType" -> "DOLLAR{causeType}"
variables in EditSource += "decrease_triggers" -> "DOLLAR{decrease_triggers}"
variables in EditSource += "effectType" -> "DOLLAR{effectType}"
variables in EditSource += "eventAction" -> "DOLLAR{eventAction}"
variables in EditSource += "event_action" -> "DOLLAR{event_action}"
variables in EditSource += "eventLabel" -> "DOLLAR{eventLabel}"
variables in EditSource += "forms" -> "DOLLAR{forms}"
variables in EditSource += "formsWords" -> "DOLLAR{formsWords}"
variables in EditSource += "increase_triggers" -> "DOLLAR{increase_triggers}"
variables in EditSource += "pathPrefix" -> "DOLLAR{pathPrefix}"
variables in EditSource += "priority" -> "DOLLAR{priority}"
variables in EditSource += "rulePriority" -> "DOLLAR{rulePriority}"
variables in EditSource += "trigger" -> "DOLLAR{trigger}"

substitutions in EditSource += sub("""DOLLAR[{]""".r, """\${""", SubAll)

//variables in EditSource += "author" -> "Lum AI",
//compile in Compile := ((compile in Compile) dependsOn (edit in EditSource)).value
(sources in EditSource) ++= (baseDirectory.value / "reader"/ "grammars" ** "*.yml").get
//targetDirectory in EditSource := (baseDirectory.value / "reader" / "target")
targetDirectory in EditSource := (baseDirectory.value / "reader" / "src" / "main" / "resources" / "org" / "parsertongue" )


editGrammars := {
  println("Copying and modifying grammars to read from resources...")
  val _ = (edit in EditSource).value
}

addCommandAlias("copyGrammars", ";clean;editsource:clean;editGrammars")

addCommandAlias("cleanTest", ";copyGrammars;test")

addCommandAlias("dockerize", ";copyGrammars;docker:publishLocal")

// addCommandAlias("pushWebappToEcr", ";webapp/ecr:createRepository;webapp/ecr:login;webapp/ecr:push")
// addCommandAlias("dockerizeWebappAndPushToEcr", ";dockerizeWebapp;pushWebappToEcr")
// // runnables
// addCommandAlias("pushToEcr", ";aws/ecr:createRepository;aws/ecr:login;aws/ecr:push")
// addCommandAlias("dockerizeAndPushToEcr", ";dockerize;pushToEcr")
// addCommandAlias("sparkify", ";clean;editsource:clean;editGrammars;compile;sparkTasks/assembly")
