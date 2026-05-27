# kmake

A command-line task runner with a type-safe **Kotlin DSL** — a more ergonomic
alternative to Makefiles and shell scripts for project automation.

You declare tasks, their dependencies, and what they do in a `tasks.kmake.kts`
file. kmake resolves the dependency graph and runs the tasks in the right order.

> **Status: early work in progress.** This is a learning project (Kotlin/JVM) being
> built step by step. The dependency engine and DSL aren't wired up yet — see
> [`BACKLOG.md`](BACKLOG.md) for the roadmap and current progress.

## Why

Makefiles have cryptic syntax and poor IDE support; full build tools like Gradle are
overkill for simple automation; shell scripts get unwieldy fast. Defining tasks in
Kotlin gives you:

- **Type safety and IDE autocomplete** — your task file is real, checked code.
- **Real control flow** — loops, conditionals, and functions, not string templating.
- **The JVM ecosystem** at your fingertips.
- **Coroutines** as a foundation for parallel execution (planned).

## The idea

A `tasks.kmake.kts` file describes your tasks. *(Target syntax — still being built; may evolve.)*

```kotlin
task("clean") {
    description = "Remove build output"
    action { sh("rm -rf build") }
}

task("build") {
    description = "Compile the project"
    dependsOn("clean")
    action { sh("./gradlew assemble") }
}

task("test") {
    description = "Run the test suite"
    dependsOn("build")
    action { sh("./gradlew test") }
}
```

Then from the command line:

```console
$ kmake test      # runs clean → build → test, each exactly once, in dependency order
$ kmake --list    # list all available tasks with their descriptions
```

## Building from source

Requires a JDK (21+). The Gradle wrapper handles the rest — no separate Gradle install needed.

```console
$ ./gradlew build   # compile and test
$ ./gradlew run     # run kmake (currently a placeholder CLI)
```

## Inspiration

- **[make](https://en.wikipedia.org/wiki/Make_(software))** (1976) — the original: targets, dependencies, topological execution.
- **[Just](https://github.com/casey/just)** — a simpler command runner (no dependency graph).
- **[Task](https://taskfile.dev/)** — YAML-based task runner (Go).
- **[Gradle](https://gradle.org/)** — the heavyweight JVM build tool whose Kotlin DSL inspired this one.

## Development

This repo is built as a guided learning project. See [`BACKLOG.md`](BACKLOG.md) for the
ordered plan and design decisions, and [`CLAUDE.md`](CLAUDE.md) for the working setup
(toolchain versions, editor/LSP notes).
