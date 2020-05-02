import Dependencies._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName

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
  organization := "com.cmartin.learn",
  scalaVersion := "2.13.2",
  libraryDependencies ++= mainAndTest,
  scalacOptions ++= basicScalacOptions,
  test in assembly := {},
  assemblyJarName in assembly := "webapp.jar"
)

lazy val templateProject = (project in file("."))
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

