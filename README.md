# kmake

A command-line task runner with a type-safe Kotlin DSL. A more ergonomic
alternative to Makefiles and shell scripts for project automation.

You declare tasks, their dependencies, and what they do in a `tasks.kmake.kts`
file. kmake resolves the dependency graph and runs the tasks in the right order,
each exactly once.

> **Status: working MVP.** Built step by step as a Kotlin/JVM learning project —
> it compiles, tests, and installs itself (see the example below). See
> [`BACKLOG.md`](BACKLOG.md) for the design decisions and roadmap.

## Why

Makefiles have cryptic syntax and poor IDE support. Full build tools like Gradle are
overkill for simple automation. Shell scripts get unwieldy fast. Defining tasks in
Kotlin gives you:

- **Checked code** — task bodies are ordinary Kotlin, with the JVM ecosystem and
  IDE support behind them, not string templating.
- **Control flow** — loops, conditionals, and functions.
- **Clear errors** — kmake names the actual problem (`Dependency cycle detected: a -> b -> a`,
  `Task 'build' depends on unknown task 'compil'`), rather than failing cryptically.
- **Coroutines** as a foundation for parallel execution (planned).

## Quick start

Requires a JDK (21+). The Gradle wrapper handles everything else. No separate Gradle
install needed.

```console
$ ./gradlew installDist                      # build the launcher
$ ./build/install/kmake/bin/kmake --list     # list tasks in ./tasks.kmake.kts
$ ./build/install/kmake/bin/kmake check      # run a task and its dependencies
```

Put the launcher on your `PATH` (symlink `build/install/kmake/bin/kmake` somewhere on it)
to just type `kmake`.

## Writing a tasks file

Tasks live in `tasks.kmake.kts`. No imports or boilerplate, `task` and `sh` are in scope
automatically. This is kmake's own task file:

```kotlin
task("check") {
    description = "Compile and test"
    dependsOn("build", "test")
}

task("clean") {
    description = "Remove build output"
    action { sh("rm -rf build") }
}

task("build") {
    description = "Compile the project"
    action { sh("./gradlew assemble") }
}

task("test") {
    description = "Run the test suite"
    dependsOn("build")
    action { sh("./gradlew test") }
}
```

`kmake check` runs `build` then `test` then `check`, in dependency order. `check` does no
work itself, it's an aggregator whose value is entirely in its edges.

### The DSL

Inside a `task("name") { ... }` block:

| Element | Purpose |
|---------|---------|
| `description = "..."` | A one-line summary, shown by `--list`. Optional. |
| `dependsOn("a", "b")` | Tasks that must run first. Referenced by name. Optional. |
| `action { ... }` | What the task does. The body is a `suspend` lambda, so it's ready for future parallelism but you can call ordinary blocking code (like `sh`) inside. Optional. |
| `sh("cmd")` | Run a shell command via `sh -c` (so pipes, globs, `&&`, and quotes all work). Output streams live. A non-zero exit aborts the run. |

## Usage

```console
kmake <task>            # run a task and everything it depends on
kmake --list   (-l)     # list all tasks with their descriptions
kmake -f <file>         # use a specific tasks file instead of discovering one
kmake --verbose (-v)    # print full stack traces on failure
kmake --help            # full help
```

If you don't pass `-f`, kmake discovers the tasks file by walking up from the current
directory until it finds a `tasks.kmake.kts` (like `git` finds `.git`), so you can run it
from anywhere inside a project.

A failing task (or a missing/unknown task, a dependency cycle, a non-zero `sh`) prints a
clear message and exits non-zero. Use `--verbose` for the stack trace.

## How it works

- **Script loading** — `tasks.kmake.kts` is a [custom Kotlin script definition](https://kotlinlang.org/docs/custom-script-deps-tutorial.html).
  An implicit receiver makes `task(...)` resolve against a `TaskRegistry` with no imports.
  the script is compiled and evaluated by the embedded Kotlin scripting host.
- **Resolution** — a depth-first traversal produces a topological order and detects cycles
  in a single pass, naming the offending path.
- **Execution** — tasks run sequentially inside a single `runBlocking`, logging start/end,
  and failing fast on the first error.

## Known limitations

These are deliberate MVP tradeoffs, not bugs:

- **Dependencies are referenced by string name.** Typos are caught at resolution time, not
  by the compiler — so `dependsOn` doesn't get autocomplete or compile-time checking. A 
  task-handle design is a candidate for v2.
- **Only true-dependency edges.** kmake can't express "run A *before* B, but only when both
  are requested" (what Gradle calls `mustRunAfter`). For example, you can't cleanly force
  `clean` to precede `build` without making `clean` a hard dependency of `build`, which
  would wipe the build dir on every compile.
- **Cold-start latency.** JVM startup plus first-run script compilation adds a noticeable
  delay versus `make`/`just`.
- **Sequential only.** No parallel execution yet (the `suspend` action type is groundwork for it).
- **Shell-dependent.** `sh` runs commands through `sh -c`, so tasks assume a POSIX shell.
- **Editor support on `.kts` is limited** compared to regular `.kt` source.

## Inspiration

- **[make](https://en.wikipedia.org/wiki/Make_(software))** (1976) — the original: targets, dependencies, topological execution.
- **[Just](https://github.com/casey/just)** — a simpler command runner (no dependency graph).
- **[Task](https://taskfile.dev/)** — YAML-based task runner (Go).
- **[Gradle](https://gradle.org/)** — the heavyweight JVM build tool whose Kotlin DSL inspired this one.

## Development

This repo is built as a guided learning project. See [`BACKLOG.md`](BACKLOG.md) for the
ordered plan and design decisions, and [`CLAUDE.md`](CLAUDE.md) for the working setup
(toolchain versions, editor/LSP notes).

```console
$ ./gradlew build                  # compile and run tests
$ ./gradlew test                   # tests only
$ ./gradlew run --args="--list"    # run from sources without installing
```
