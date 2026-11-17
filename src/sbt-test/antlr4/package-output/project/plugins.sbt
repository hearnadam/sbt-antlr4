sys.props.get("plugin.version") match {
  case Some(v) => addSbtPlugin("ai.hearn" % "sbt-antlr4" % v)
  case None    => sys.error("plugin.version system property not set")
}
