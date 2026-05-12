# sbt-antlr4

SBT plugin for ANTLR4 code generation.

Resolves the ANTLR4 tool jar via Coursier by default.
For vendored or non-Maven environments, provide jars manually.

## Setup

`project/plugins.sbt`:

```scala
addSbtPlugin("ai.hearn" % "sbt-antlr4" % "0.1.0")
```

`build.sbt`:

```scala
enablePlugins(Antlr4Plugin)
```

Place `.g4` grammars in `src/main/antlr4/`. Run `sbt compile`.

## Settings

All scoped under `Antlr4`:

| Setting | Type | Default | Description |
|---------|------|---------|-------------|
| `antlr4Version` | `String` | `"4.13.2"` | Auto-resolved version |
| `antlr4ToolClasspath` | `Seq[File]` | `Seq.empty` | Manual jars — disables all auto-resolution when non-empty |
| `antlr4Source` | `File` | `src/main/antlr4` | Grammar directory |
| `antlr4Output` | `File` | `src_managed/main/antlr4` | Generated source output |
| `antlr4Package` | `Option[String]` | `None` | Package for generated code |
| `antlr4Listener` | `Boolean` | `true` | Generate listener |
| `antlr4Visitor` | `Boolean` | `false` | Generate visitor |
| `antlr4Language` | `Option[String]` | `None` | Target language (`Java`, `Python3`, `Go`, etc.) |
| `antlr4Library` | `Option[File]` | `None` | Directory for imported grammars / `.tokens` |
| `antlr4FatalWarnings` | `Boolean` | `false` | Treat warnings as errors |
| `antlr4Options` | `Map[String, String]` | `Map.empty` | `-D<key>=<value>` overrides |
| `antlr4ExtraArgs` | `Seq[String]` | `Seq.empty` | Raw CLI args escape hatch |

## Examples

```scala
enablePlugins(Antlr4Plugin)
Antlr4 / antlr4Package := Some("com.example.parser")
Antlr4 / antlr4Visitor := true
```

Pin a specific version:

```scala
Antlr4 / antlr4Version := "4.12.0"
```

Manual classpath (disables Coursier and runtime auto-add):

```scala
Antlr4 / antlr4ToolClasspath := (file("/opt/antlr/lib") ** "*.jar").get
```

Grammar imports:

```scala
Antlr4 / antlr4Library := Some(baseDirectory.value / "src" / "main" / "antlr4" / "imports")
```

Extra flags:

```scala
Antlr4 / antlr4ExtraArgs := Seq("-encoding", "UTF-8", "-atn")
```

## Multi-module project

```scala
lazy val parser = (project in file("modules/parser"))
  .enablePlugins(Antlr4Plugin)
  .settings(
    Antlr4 / antlr4Package := Some("com.example.parser"),
    Antlr4 / antlr4Visitor := true,
  )

lazy val core = (project in file("modules/core"))
  .dependsOn(parser)
```

## Migrating from `com.simplytyped` 0.8.x

| Old | New |
|-----|-----|
| `addSbtPlugin("com.simplytyped" ...)` | `addSbtPlugin("ai.hearn" ...)` |
| `antlr4Version in Antlr4` | `Antlr4 / antlr4Version` |
| `antlr4PackageName in Antlr4` | `Antlr4 / antlr4Package` |
| `antlr4GenListener in Antlr4` | `Antlr4 / antlr4Listener` |
| `antlr4GenVisitor in Antlr4` | `Antlr4 / antlr4Visitor` |
| `antlr4TreatWarningsAsErrors in Antlr4` | `Antlr4 / antlr4FatalWarnings` |
| `Antlr4 / sourceDirectory` | `Antlr4 / antlr4Source` |
| `Antlr4 / javaSource` | `Antlr4 / antlr4Output` |
| `antlr4RuntimeDependency` | Removed — auto-added when using Coursier |
| `antlr4Dependency` | Removed — handled by `antlr4Version` |

Key differences:
- ANTLR tool jars and `antlr4-runtime` resolve automatically via Coursier.
- Setting `antlr4ToolClasspath` disables all auto-resolution (enterprise/vendored environments manage deps externally).
- `-Xexact-output-dir` is always on.

## License

Apache License 2.0.
