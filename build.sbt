import Dependencies._

lazy val basicScalacOptions = Seq(       // some of the Rob Norris tpolecat options
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-explaintypes",                     // Explain type errors in more detail.
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",
    "-language:postfixOps"
  )

lazy val commonSettings = Seq(
    organization := "com.cmartin.learn",
    scalaVersion := "2.13.1",
    libraryDependencies ++= mainAndTest,
    scalacOptions ++= basicScalacOptions,
    test in assembly := {}
)

lazy val templateProject = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
      commonSettings,
      name := "tapir-learn",
      testFrameworks ++= Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )  .settings(coverageExcludedPackages := "<empty>;.*ServerApp.*")
