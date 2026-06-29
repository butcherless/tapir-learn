import Dependencies.*
import com.github.sbt.git.SbtGit.GitKeys.{gitDescribedVersion, useGitDescribe}

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := Versions.scala
ThisBuild / organization := "com.cmartin.learn"

lazy val basicScalacOptions = Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-unchecked",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps"
)

lazy val commonSettings = Seq(
  libraryDependencies ++= commonTest,
  scalacOptions       ++= basicScalacOptions
)

// ─── Modules ────────────────────────────────────────────────────────────────

lazy val `aviation-root` = project
  .in(file("."))
  .aggregate(
    `aviation-core`,
    `aviation-repository`,
    `aviation-service`,
    `aviation-api`,
    `aviation-web`,
    `tapir-webapp`
  )
  .settings(
    name           := "aviation-root",
    publish / skip := true
  )

lazy val `tapir-webapp` = project
  .in(file("tapir-webapp"))
  .settings(
    commonSettings,
    libraryDependencies ++= mainAndTest,
    assembly / assemblyJarName := "tapir-webapp.jar",
    name                       := "tapir-webapp",
    coverageExcludedPackages   := "<empty>;.*ServerApp.*"
  )
  .settings(BuildInfoSettings.value*)
  .enablePlugins(BuildInfoPlugin, GitVersioning)

lazy val `aviation-web` = project
  .in(file("aviation-web"))
  .settings(
    commonSettings,
    libraryDependencies ++= webMain,
    name                 := "aviation-web",
    Compile / run / fork := true,
    parallelExecution    := false
  )
  .dependsOn(`aviation-api`)

lazy val `aviation-core` = project
  .in(file("aviation-core"))
  .settings(
    commonSettings,
    libraryDependencies      ++= coreMain,
    name                     := "aviation-core",
    coverageExcludedPackages := "<empty>;.*Configuration.*"
  )

lazy val `aviation-repository` = project
  .in(file("aviation-repository"))
  .settings(
    commonSettings,
    libraryDependencies ++= repoMain ++ h2Test,
    name              := "aviation-repository",
    parallelExecution := false
  )
  .dependsOn(`aviation-core`)

lazy val `aviation-service` = project
  .in(file("aviation-service"))
  .settings(
    commonSettings,
    libraryDependencies ++= serviceTest,
    name              := "aviation-service",
    parallelExecution := false
  )
  .dependsOn(`aviation-core`, `aviation-repository`, `aviation-test-utils` % "test->compile")

lazy val `aviation-api` = project
  .in(file("aviation-api"))
  .settings(
    commonSettings,
    libraryDependencies        ++= apiMain ++ apiTest,
    assembly / assemblyJarName := "aviation-webapp.jar",
    name                       := "aviation-api",
    coverageExcludedPackages   := "<empty>;.*ServerApp.*"
  )
  .settings(AviationBuildInfoSettings.value*)
  .dependsOn(`aviation-core`)
  .enablePlugins(BuildInfoPlugin, GitVersioning)

lazy val `aviation-test-utils` = project
  .in(file("aviation-test-utils"))
  .settings(
    assembly / assemblyJarName := "aviation-test-utils.jar",
    name                       := "aviation-test-utils"
  )
  .dependsOn(`aviation-core`, `aviation-repository`)

Global / excludeLintKeys ++= Set(useGitDescribe, gitDescribedVersion)

// ─── Aliases ────────────────────────────────────────────────────────────────

addCommandAlias("xcoverage",    "clean;coverage;test;coverageReport")
addCommandAlias("xreload",      "clean;reload")
addCommandAlias("xupdate",      "clean;update")
addCommandAlias("xdup",         "dependencyUpdates")
addCommandAlias("xcompile",     "~aviation-root/cls ; compile")
addCommandAlias("xtestCompile", "~aviation-root/cls ; Test/compile")

// ─── Tasks ──────────────────────────────────────────────────────────────────

lazy val cls = taskKey[Unit]("Prints a separator")
LocalRootProject / cls := Def.uncached {
  val downArrow     = "↓"
  val brs           = "\n".repeat(2)
  val message       = "BUILD BEGINS HERE"
  val spacedMessage = message.mkString(s"$downArrow ", " ", s" $downArrow")
  val chars         = "*".repeat(spacedMessage.length())
  println(s"$brs$chars")
  println(spacedMessage)
  println(s"$chars$brs ")
}

// ─── Assembly ───────────────────────────────────────────────────────────────

ThisBuild / assemblyMergeStrategy := {
  case PathList("org", "json4s", _*)                                   => MergeStrategy.first
  case PathList("META-INF", "versions", _, "module-info.class")        => MergeStrategy.discard
  case PathList("META-INF", "versions", _, "OSGI-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "services", _*)                            => MergeStrategy.concat
  case "META-INF/io.netty.versions.properties"                         => MergeStrategy.first
  case "module-info.class"                                             => MergeStrategy.discard
  case "deriving.conf"                                                 => MergeStrategy.first
  case x                                                               =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
