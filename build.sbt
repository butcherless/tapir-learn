import Dependencies.*
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "2.13.9"
ThisBuild / organization := "com.cmartin.learn"

lazy val basicScalacOptions = Seq( // some of the Rob Norris tpolecat options
  "-deprecation",          // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8",                 // Specify character encoding used by source files.
  "-explaintypes",         // Explain type errors in more detail.
  "-explaintypes",         // Explain type errors in more detail.
  "-unchecked",            // Enable additional warnings where generated code depends on assumptions.
  "-feature",              // Emit warning and location for usages of features that should be imported explicitly.
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions",
  "-language:postfixOps"
)

lazy val commonSettings = Seq(
  libraryDependencies ++= commonTest,
  scalacOptions ++= basicScalacOptions
)

lazy val `aviation-root` = project
  .in(file("."))
  .aggregate(
    `aviation-core`,
    `aviation-repository`,
    `aviation-service`,
    `aviation-api`,
    `tapir-webapp`
  )

lazy val `tapir-webapp` = project
  .in(file("tapir-webapp"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= mainAndTest,
    assemblyJarName       := "tapir-webapp.jar",
    name                  := "tapir-webapp",
    assemblyMergeStrategy := {
      // case PathList("io", "netty", "netty-all", xs @ _*) => MergeStrategy.first
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first
      case "module-info.class"                     => MergeStrategy.first
      case x                                       =>
        val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
  .settings(coverageExcludedPackages := "<empty>;.*ServerApp.*")
  .settings(BuildInfoSettings.value)
  // plugins
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)

lazy val `aviation-web` = project
  .in(file("aviation-web"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= webMain,
    name                 := "aviation-web",
    Compile / run / fork := true,
    parallelExecution    := false
  )
  .dependsOn(`aviation-api`)

lazy val `aviation-core` = project
  .in(file("aviation-core"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= coreMain,
    name := "aviation-core"
  )
  .settings(coverageExcludedPackages := "<empty>;.*Configuration.*")

lazy val `aviation-repository` = project
  .in(file("aviation-repository"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= repoMain ++ h2Test,
    name              := "aviation-repository",
    parallelExecution := false
  )
  .dependsOn(`aviation-core`)

lazy val `aviation-service` = project
  .in(file("aviation-service"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= serviceTest,
    name              := "aviation-service",
    parallelExecution := false
  )
  .dependsOn(`aviation-core`, `aviation-repository`, `aviation-test-utils` % "test->compile")
//TODO repository interface / implementation modules
//TODO .dependsOn(`aviation-core`, `aviation-repository` % "test->compile")

lazy val `aviation-api` = project
  .in(file("aviation-api"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    libraryDependencies ++= apiMain ++ apiTest,
    assemblyJarName := "aviation-webapp.jar",
    name            := "aviation-api"
  )
  .dependsOn(`aviation-core`)
  .settings(coverageExcludedPackages := "<empty>;.*ServerApp.*")
  .settings(AviationBuildInfoSettings.value)
  // plugins
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)

lazy val `aviation-test-utils` = project
  .in(file("aviation-test-utils"))
  .settings(
//    libraryDependencies ++= apiMain ++ apiTest,
    assemblyJarName := "aviation-test-utils.jar",
    name            := "aviation-test-utils"
  )
  .dependsOn(`aviation-core`, `aviation-repository`)

// clear screen and banner
lazy val cls = taskKey[Unit]("Prints a separator")
cls := {
  val brs     = "\n".repeat(2)
  val message = "* B U I L D   B E G I N S   H E R E *"
  val chars   = "*".repeat(message.length())
  println(s"$brs$chars")
  println("* B U I L D   B E G I N S   H E R E *")
  println(s"$chars$brs ")
}

addCommandAlias("xcoverage", "clean;coverage;test;coverageReport")
addCommandAlias("xreload", "clean;reload")
addCommandAlias("xstart", "clean;reStart")
addCommandAlias("xstop", "reStop;clean")
addCommandAlias("xupdate", "clean;update")
addCommandAlias("xdup", "dependencyUpdates")
addCommandAlias("xcompile", "~aviation-root/cls ; compile")
addCommandAlias("xtestCompile", "~aviation-root/cls ; Test/compile")
