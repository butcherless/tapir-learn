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
  libraryDependencies ++= mainAndTest,
  scalacOptions ++= basicScalacOptions,
  assemblyJarName := "tapir-learn-webapp.jar"
)

lazy val tapirLearn = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    name := "tapir-learn"
  )
  .settings(coverageExcludedPackages := "<empty>;.*ServerApp.*")
  .settings(BuildInfoSettings.value)
  // plugins
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)

lazy val cls = taskKey[Unit]("Prints a separator")
cls := {
  val brs = "\n".repeat(4)
  val chars = "*".repeat(37)
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
