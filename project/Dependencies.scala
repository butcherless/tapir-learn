import sbt._

object Dependencies {
  lazy val akkaHttpVersion = "10.1.12"
  lazy val akkaVersion = "2.6.5"
  lazy val sttpVersion = "2.1.2"
  lazy val tapirVersion = "0.15.0"

  lazy val json4sVersion = "3.6.8"
  lazy val logbackVersion = "1.2.3"
  lazy val zioVersion = "1.0.0-RC19-1"

  lazy val scalatestVersion = "3.1.2"

  val mainAndTest = Seq(
    // A K K A
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,

    // S T T P
    "com.softwaremill.sttp.client" %% "core" % sttpVersion % "it, test",
    "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % sttpVersion % "it, test",
    "com.softwaremill.sttp.client" %% "async-http-client-backend-zio-streams" % sttpVersion % "it, test",
    "com.softwaremill.sttp.client" %% "circe" % sttpVersion,

    // T A P I R
    "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion,

    "ch.qos.logback" % "logback-classic" % logbackVersion exclude("org.slf4j", "slf4j-api"),
    "dev.zio" %% "zio" % zioVersion,

    "org.json4s" %% "json4s-core" % json4sVersion,
    "org.json4s" %% "json4s-native" % json4sVersion,

    // T E S T
    "org.scalatest" %% "scalatest" % scalatestVersion % "it, test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,


    "dev.zio" %% "zio-test" % zioVersion % "test",
    "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
  )
}
