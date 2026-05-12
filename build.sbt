ThisBuild / organization := "ai.hearn"
ThisBuild / version := "0.1.0"
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/hearnadam/sbt-antlr4"))
ThisBuild / developers := List(
  Developer("hearnadam", "Adam Hearn", "adam@hearn.ai", url("https://hearn.ai"))
)

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-antlr4",
  )
