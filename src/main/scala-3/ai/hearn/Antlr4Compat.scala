package ai.hearn

import sbt.*
import sbt.Keys.Classpath
import sbt.librarymanagement.{Configuration, UpdateReport}

private[hearn] object Antlr4Compat:
  def managedJars(
      config: Configuration,
      jarTypes: Set[String],
      report: UpdateReport,
      converter: xsbti.FileConverter
  ): Classpath =
    Classpaths.managedJars(config, jarTypes, report, converter)
