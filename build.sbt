import Dependencies._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "com.cmartin.learn"

lazy val basicScalacOptions = Seq( // some of the Rob Norris tpolecat options
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-explaintypes", // Explain type errors in more detail.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions",
  "-language:postfixOps"
)

lazy val commonSettings = Seq(
  libraryDependencies ++= commonTest,
  scalacOptions ++= basicScalacOptions
)

lazy val `aviation-root` = (project in file("."))
  .aggregate(
    `aviation-core`,
    `aviation-repository`,
    `aviation-service`,
    `aviation-api`,
    `tapir-learn`
  )

lazy val `tapir-learn` = (project in file("tapir-learn"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= mainAndTest,
    assemblyJarName := "tapir-learn-webapp.jar",
    name := "tapir-learn"
  )
  .settings(coverageExcludedPackages := "<empty>;.*ServerApp.*")
  .settings(BuildInfoSettings.value)
  // plugins
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)

lazy val `aviation-core` = (project in file("aviation-core"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= commonMain,
    name := "aviation-core"
  )
  .settings(coverageExcludedPackages := "<empty>;.*Configuration.*")

lazy val `aviation-repository` = (project in file("aviation-repository"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= repoMain ++ repoTest,
    name := "aviation-repository",
    parallelExecution := false
  )
  .dependsOn(`aviation-core`)

lazy val `aviation-service` = (project in file("aviation-service"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= repoMain ++ repoTest,
    name := "aviation-service",
    parallelExecution := false
  )
  .dependsOn(`aviation-core`, `aviation-repository` % "test->compile")

lazy val `aviation-api` = (project in file("aviation-api"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= apiMain ++ apiTest,
    assemblyJarName := "aviation-webapp.jar",
    name := "aviation-api"
  )
  .dependsOn(`aviation-core`)
  .settings(coverageExcludedPackages := "<empty>;.*ServerApp.*")
  .settings(AviationBuildInfoSettings.value)
  // plugins
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)

// clear screen and banner
lazy val cls = taskKey[Unit]("Prints a separator")
cls := {
  val brs = "\n".repeat(2)
  val message = "* B U I L D   B E G I N S   H E R E *"
  val chars = "*".repeat(message.length())
  println(s"$brs$chars")
  println("* B U I L D   B E G I N S   H E R E *")
  println(s"$chars$brs ")
}

Global / onChangedBuildSource := ReloadOnSourceChanges

addCommandAlias("xcoverage", "clean;coverage;test;coverageReport")
addCommandAlias("xreload", "clean;reload")
addCommandAlias("xstart", "clean;reStart")
addCommandAlias("xstop", "reStop;clean")
addCommandAlias("xupdate", "clean;update")
addCommandAlias("xdup", "dependencyUpdates")
