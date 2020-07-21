import Dependencies._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName


ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "com.cmartin.learn"

lazy val basicScalacOptions = Seq( // some of the Rob Norris tpolecat options
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8", // Specify character encoding used by source files.
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
  test in assembly := {},
  assemblyJarName in assembly := "tapir-learn-webapp.jar"
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

