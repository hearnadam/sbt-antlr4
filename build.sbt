ThisBuild / organization := "ai.hearn"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / scalaVersion := "3.8.4"
ThisBuild / crossScalaVersions := Seq("3.8.4", "2.12.21")
ThisBuild / publishTo := {
  if (isSnapshot.value)
    Some("central-snapshots" at "https://central.sonatype.com/repository/maven-snapshots/")
  else
    localStaging.value
}
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/hearnadam/sbt-antlr4"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/hearnadam/sbt-antlr4"),
    "scm:git@github.com:hearnadam/sbt-antlr4.git"
  )
)
ThisBuild / developers := List(
  Developer("hearnadam", "Adam Hearn", "adam@hearn.ai", url("https://hearn.ai"))
)

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-antlr4",
    (pluginCrossBuild / sbtVersion) := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.13.0"
        case _      => "2.0.7"
      }
    },
    addSbtPlugin("com.github.sbt" % "sbt2-compat" % "0.2.0"),
    scriptedLaunchOpts ++= Seq(
      "-Xmx1024M",
      s"-Dplugin.version=${version.value}"
    ),
    scriptedBufferLog := false
  )
