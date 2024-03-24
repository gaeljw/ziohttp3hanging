logLevel := Level.Warn

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.16")

// scala-xml breaking compatibility but not really (https://eed3si9n.com/sbt-1.8.0)
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

// Dynamic versioning from git
addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.0.1")

addSbtPlugin("com.here.platform" % "sbt-bom" % "1.0.11")
