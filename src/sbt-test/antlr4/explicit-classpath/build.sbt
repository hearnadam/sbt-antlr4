enablePlugins(Antlr4Plugin)

scalaVersion := "3.8.3"

// Resolve the ANTLR tool jars through a separate ivy config and feed them
// in via antlr4ToolClasspath — this exercises the explicit-classpath branch
// (no antlr4Version-driven auto-resolution).
lazy val Antlr4Tool = config("antlr4tool").hide
ivyConfigurations += Antlr4Tool

Antlr4 / antlr4Version := None
Antlr4 / antlr4Output := baseDirectory.value / "target" / "antlr4-generated"

libraryDependencies ++= Seq(
  "org.antlr" % "antlr4"         % "4.13.2" % Antlr4Tool,
  "org.antlr" % "antlr4-runtime" % "4.13.2"
)

Antlr4 / antlr4ToolClasspath := {
  update.value.matching(configurationFilter(Antlr4Tool.name))
}

Antlr4 / antlr4Package  := Some("example.expr")
Antlr4 / antlr4Listener := true
