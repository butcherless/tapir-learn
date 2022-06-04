import sbt._

object Dependencies {

  val commonMain = Seq()

  val commonTest = Seq(
    "org.scalatest" %% "scalatest" % Versions.scalatest % "it, test"
  )

  val coreMain = Seq(
    "ch.qos.logback" % "logback-classic" % Versions.logback,
    "dev.zio" %% "zio-logging-slf4j" % Versions.zioLogging,
    "dev.zio" %% "zio-prelude" % Versions.zioPrelude
  )

  val coreTest = Seq()

  val repoMain = Seq(
    "com.typesafe.slick" %% "slick" % Versions.slick exclude ("org.slf4j", "slf4j-api") exclude ("com.typesafe", "config"),
    "com.typesafe.slick" %% "slick-hikaricp" % Versions.slick exclude ("org.slf4j", "slf4j-api")
  )

  val h2Test = Seq(
    "com.h2database" % "h2" % Versions.h2 % Test
  )

  val serviceTest = Seq(
    "org.scalamock" %% "scalamock" % Versions.scalaMock % Test,
    "com.h2database" % "h2" % Versions.h2 % Test
  )

  val apiMain = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.tapir
  )

  val apiTest = Seq(
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % Test,
    "com.typesafe.akka" %% "akka-testkit" % Versions.akka % Test,
    "org.scalamock" %% "scalamock" % Versions.scalaMock % Test
  )

  val mainAndTest = Seq(
    // T A P I R
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-json4s" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % Versions.tapir,
    "com.typesafe.akka" %% "akka-slf4j" % Versions.akka,
    "ch.qos.logback" % "logback-classic" % Versions.logback,
    "com.github.mlangc" %% "slf4zio" % Versions.slf4zio,
    "dev.zio" %% "zio-prelude" % Versions.zioPrelude,
    "org.json4s" %% "json4s-ext" % Versions.json4s,
    "org.json4s" %% "json4s-native" % Versions.json4s,
    /*
       T E S T
     */
    "org.scalatest" %% "scalatest" % Versions.scalatest % "it, test",
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % Test,
    "com.typesafe.akka" %% "akka-testkit" % Versions.akka % Test,
    // S T T P
    "com.softwaremill.sttp.client3" %% "core" % Versions.sttp, // % "it, test",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % Versions.sttp, // % "it, test",
    "com.softwaremill.sttp.client3" %% "circe" % Versions.sttp % "it, test"
  )
}
