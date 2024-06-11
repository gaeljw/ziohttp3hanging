import com.here.bom.Bom

scalaVersion := "2.13.13"

// specify java version
lazy val jdkVersion = "17"
scalacOptions += "-release:%s".format(jdkVersion)
javacOptions ++= Seq("-source", jdkVersion, "-target", jdkVersion)

val zioConfigVersion = "4.0.1"
val zioHttpVersion = "3.0.0-RC8"

lazy val nettyDeps = Bom.dependencies("io.netty" % "netty-bom" % "4.1.89.Final")

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(name := "some-app", version := "0.0.1-SNAPSHOT")
  // Load BOMs
  .settings(nettyDeps)
  .settings(
    libraryDependencies ++= Seq(
      // Config
      "dev.zio" %% "zio-config" % zioConfigVersion,
      "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,

      // ZIO
      "dev.zio" %% "zio" % "2.0.21",
      "dev.zio" %% "zio-jdbc" % "0.1.1",
      "dev.zio" %% "zio-json" % "0.6.2",
      "dev.zio" %% "zio-cli" % "0.5.0",

      // ZIO Http
      "dev.zio" %% "zio-http" % zioHttpVersion,

      // Logging
      "ch.qos.logback" % "logback-classic" % "1.5.3",

      // ---------- Tests ----------
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,

      // Pact
      "io.github.jbwheatley" %% "pact4s-scalatest" % "0.10.0" % Test,

      // ZIO
      "dev.zio" %% "zio-test" % "2.0.21" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.21" % Test,
      "dev.zio" %% "zio-test-magnolia" % "2.0.21" % Test,

      // ZIO Http
      "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test,

      // SBT - Run jUnit4 tests
      "com.github.sbt" % "junit-interface" % "0.13.3" % Test
    )
  )
  .settings(
    dependencyOverrides ++= nettyDeps.key.value
  )

scalacOptions ++= CompilationFlags.scalaFlags

// Enforce sequential execution of tests, otherwise we have issues with the fake Mailbox
Test / parallelExecution := false

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
