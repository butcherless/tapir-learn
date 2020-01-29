import sbt._

object Dependencies {
  lazy val akkaHttpVersion = "10.1.11"
  lazy val akkaVersion = "2.6.1"
  lazy val tapirVersion = "0.12.18"


  lazy val logbackVersion = "1.2.3"
  lazy val zioVersion = "1.0.0-RC17"

  lazy val scalatestVersion = "3.1.0"

  val mainAndTest = Seq(
    // A K K A
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,

    // T A P I R
    "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion,

    "ch.qos.logback" % "logback-classic" % logbackVersion exclude ("org.slf4j", "slf4j-api"),
    "dev.zio" %% "zio" % zioVersion,

    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,


    "dev.zio" %% "zio-test" % zioVersion % "test",
    "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
  )
}
