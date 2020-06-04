name := "logx-reader-rest-api"

libraryDependencies ++= {
  val procVersion = ""
  Seq(
    // see https://www.playframework.com/documentation/2.6.x/WSMigration26
    // and https://www.playframework.com/documentation/2.6.x/ScalaWS
    ws,
    guice,
    // see https://www.playframework.com/documentation/2.6.x/WsCache for configuration instructions
    //ehcache,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test,
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "ai.lum" %% "common" % "0.0.8"
  )
}
