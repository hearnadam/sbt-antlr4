/*
 * Licensed under the Apache License, Version 2.0.
 * See: http://www.apache.org/licenses/LICENSE-2.0
 */

package ai.hearn

import sbt._
import sbt.Keys._
import scala.sys.process.Process

object Antlr4Plugin extends AutoPlugin {
  object autoImport {
    val Antlr4 = config("antlr4")
    val antlr4Generate = taskKey[Seq[File]]("Generate sources from ANTLR4 grammars")
    val antlr4Version = settingKey[String]("ANTLR4 version to auto-resolve")
    val antlr4ToolClasspath = settingKey[Seq[File]]("ANTLR4 tool jars — when non-empty, skips Coursier and runtime auto-add")
    val antlr4Source = settingKey[File]("Directory containing .g4 grammar files")
    val antlr4Output = settingKey[File]("Output directory for generated sources")
    val antlr4Package = settingKey[Option[String]]("Package/namespace for generated code")
    val antlr4Listener = settingKey[Boolean]("Generate parse tree listener")
    val antlr4Visitor = settingKey[Boolean]("Generate parse tree visitor")
    val antlr4Language = settingKey[Option[String]]("Target language (Java, CSharp, Python3, Go, Cpp, etc.)")
    val antlr4Library = settingKey[Option[File]]("Directory for imported grammars and .tokens files")
    val antlr4FatalWarnings = settingKey[Boolean]("Treat warnings as errors")
    val antlr4Options = settingKey[Map[String, String]]("Grammar-level -D<key>=<value> overrides")
    val antlr4ExtraArgs = settingKey[Seq[String]]("Additional raw CLI arguments passed to the ANTLR4 tool")
  }
  import autoImport._

  override def trigger = noTrigger
  override def requires = plugins.JvmPlugin

  private def generateTask: Def.Initialize[Task[Seq[File]]] = Def.task {
    val explicit = (Antlr4 / antlr4ToolClasspath).value
    val managed = (Antlr4 / managedClasspath).value.files
    val cp = if (explicit.nonEmpty) explicit else managed
    val outputDir = (Antlr4 / antlr4Output).value
    val sourceDir = (Antlr4 / antlr4Source).value
    val pkg = (Antlr4 / antlr4Package).value
    val listener = (Antlr4 / antlr4Listener).value
    val visitor = (Antlr4 / antlr4Visitor).value
    val language = (Antlr4 / antlr4Language).value
    val libDir = (Antlr4 / antlr4Library).value
    val options = (Antlr4 / antlr4Options).value
    val werror = (Antlr4 / antlr4FatalWarnings).value
    val extra = (Antlr4 / antlr4ExtraArgs).value
    val log = streams.value.log
    val cacheDir = streams.value.cacheDirectory / "antlr4"

    if (cp.isEmpty) {
      sys.error(
        "ANTLR4 tool classpath is empty. Either:\n" +
        "  1. Set `Antlr4 / antlr4Version` to auto-resolve (e.g. \"4.13.2\"), or\n" +
        "  2. Override `Antlr4 / antlr4ToolClasspath` to provide jars manually."
      )
    }

    val grammars = (sourceDir ** "*.g4").get.toSet
    if (grammars.isEmpty) {
      log.debug(s"No .g4 files in $sourceDir — skipping ANTLR4 generation")
      Seq.empty
    } else {
      val cachedFn = FileFunction.cached(cacheDir) { (_: Set[File]) =>
        outputDir.mkdirs()
        val exitCode = runAntlr(
          classpath = cp,
          srcFiles = grammars,
          outputDir = outputDir,
          packageName = pkg,
          listener = listener,
          visitor = visitor,
          language = language,
          libDir = libDir,
          options = options,
          werror = werror,
          extra = extra,
          log = log
        )
        if (exitCode != 0) sys.error(s"ANTLR4 code generation failed (exit code $exitCode)")
        (outputDir ** ("*.java" | "*.cs" | "*.py" | "*.js" | "*.ts" | "*.go" | "*.cpp" | "*.h" | "*.swift" | "*.dart")).get.toSet
      }
      cachedFn(grammars).toSeq
    }
  }

  private def runAntlr(
      classpath: Seq[File],
      srcFiles: Set[File],
      outputDir: File,
      packageName: Option[String],
      listener: Boolean,
      visitor: Boolean,
      language: Option[String],
      libDir: Option[File],
      options: Map[String, String],
      werror: Boolean,
      extra: Seq[String],
      log: Logger
  ): Int = {
    val cpStr = classpath.map(_.getPath).mkString(java.io.File.pathSeparator)
    val args = Vector.newBuilder[String]
    args += "-cp" += cpStr += "org.antlr.v4.Tool"
    args += "-o" += outputDir.getPath
    args += "-Xexact-output-dir"
    packageName.foreach { p => args += "-package" += p }
    libDir.foreach { d => args += "-lib" += d.getPath }
    language.foreach { lang => args += s"-Dlanguage=$lang" }
    options.foreach { case (k, v) => args += s"-D$k=$v" }
    args += (if (listener) "-listener" else "-no-listener")
    args += (if (visitor) "-visitor" else "-no-visitor")
    if (werror) args += "-Werror"
    extra.foreach(a => args += a)
    srcFiles.foreach(f => args += f.getPath)

    val cmd = args.result()
    log.info(s"ANTLR4: java ${cmd.mkString(" ")}")
    Process("java", cmd) ! log
  }

  override def projectSettings: Seq[Setting[_]] = inConfig(Antlr4)(Seq(
    antlr4Version := "4.13.2",
    antlr4ToolClasspath := Seq.empty,
    antlr4Source := (Compile / sourceDirectory).value / "antlr4",
    antlr4Output := (Compile / sourceManaged).value / "antlr4",
    antlr4Package := None,
    antlr4Listener := true,
    antlr4Visitor := false,
    antlr4Language := None,
    antlr4Library := None,
    antlr4FatalWarnings := false,
    antlr4Options := Map.empty,
    antlr4ExtraArgs := Seq.empty,
    managedClasspath := Classpaths.managedJars(configuration.value, classpathTypes.value, update.value),
    antlr4Generate := generateTask.value
  )) ++ Seq(
    ivyConfigurations += Antlr4,
    libraryDependencies ++= {
      if ((Antlr4 / antlr4ToolClasspath).value.isEmpty) {
        val ver = (Antlr4 / antlr4Version).value
        Seq(
          "org.antlr" % "antlr4" % ver % Antlr4,
          "org.antlr" % "antlr4-runtime" % ver
        )
      } else Seq.empty
    },
    Compile / managedSourceDirectories += (Antlr4 / antlr4Output).value,
    Compile / sourceGenerators += (Antlr4 / antlr4Generate).taskValue,
    cleanFiles += (Antlr4 / antlr4Output).value
  )
}
