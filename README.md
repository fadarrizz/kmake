# kmake

Build a command-line task runner written in Kotlin that lets developers define tasks, their dependencies, and execution rules in a Kotlin-based DSL — providing a more type-safe, ergonomic alternative to shell scripts and Makefiles for project automation.
What it's based on
The core concept comes from make (1976, Unix), the original build automation tool. make lets you declare:

Targets (named tasks)
Dependencies (other targets that must run first)
Recipes (shell commands to execute)

make then computes a dependency graph and runs tasks in topological order, skipping work that's already up-to-date (based on file timestamps).
Modern descendants worth looking at for inspiration:

Gradle (Kotlin/Groovy DSL, JVM-focused, heavyweight)
Just (a simpler command runner, no dependency graph)
Task (YAML-based, Go)
Bazel / Buck (hermetic, large-scale)
npm scripts (minimal, no dependencies between scripts)

Where your tool fits
The gap: Makefiles have cryptic syntax and poor IDE support; Gradle is overkill for simple automation; shell scripts get unwieldy fast. A Kotlin-based DSL gives you:

Type safety and IDE autocomplete
Real control flow (loops, conditionals, functions)
Easy access to the JVM ecosystem
Coroutines for parallel task execution

Core capabilities to consider for the backlog

Task definition DSL (name, description, dependencies, action block)
Dependency graph resolution and cycle detection
Parallel execution of independent tasks
File-based incremental builds (skip if inputs unchanged)
CLI argument parsing and task discovery
Output streaming and logging
Configuration loading (a tasks.kt or similar entry point — likely via Kotlin scripting / kotlinc -script)
