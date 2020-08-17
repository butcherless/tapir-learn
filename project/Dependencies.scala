import sbt._

object Dependencies {

  val mainAndTest = Seq(
    // A K K A
    "com.typesafe.akka" %% "akka-actor"       % Versions.akka,
    "com.typesafe.akka" %% "akka-actor-typed" % Versions.akka,
    "com.typesafe.akka" %% "akka-http"        % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-stream"      % Versions.akka,
    // S T T P
    "com.softwaremill.sttp.client" %% "core"                          % Versions.sttp % "it, test",
    "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % Versions.sttp % "it, test",
    "com.softwaremill.sttp.client" %% "circe"                         % Versions.sttp,
    // T A P I R
    "com.softwaremill.sttp.tapir" %% "tapir-core"                 % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"     % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"           % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"         % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"   % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % Versions.tapir,
    "io.circe"                    %% "circe-generic-extras"       % Versions.circe,
    "ch.qos.logback"               % "logback-classic"            % Versions.logback exclude ("org.slf4j", "slf4j-api"),
    "dev.zio"                     %% "zio"                        % Versions.zio,
    "org.json4s"                  %% "json4s-core"                % Versions.json4s,
    "org.json4s"                  %% "json4s-native"              % Versions.json4s,
    // T E S T
    "org.scalatest"     %% "scalatest"         % Versions.scalatest % "it, test",
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp  % Test,
    "com.typesafe.akka" %% "akka-testkit"      % Versions.akka      % Test,
    "dev.zio"           %% "zio-test"          % Versions.zio       % "test",
    "dev.zio"           %% "zio-test-sbt"      % Versions.zio       % "test"
  )
}
