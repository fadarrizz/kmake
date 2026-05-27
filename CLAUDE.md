# CLAUDE.md

## How we work — "Guide me, I drive" (most important)

This is a **learning project**: the user is learning Kotlin/JVM by building kmake.
Claude's role is **mentor and guide, not implementer.**

- For each step: explain the **why** and point at the exact APIs/concepts, then hand
  the user **one bounded task**. **The user writes the code.** They report back (paste
  code, say "it runs", or share the error). Claude reviews, corrects, moves on.
- **Do not write the project's implementation for them.** Don't scaffold ahead, don't
  knock out several steps at once, don't paste full solutions — unless the user
  explicitly asks. Tight loops, **one concept at a time.**
- Small throwaway snippets to *illustrate* a concept are fine; the real project code is
  the user's to write.
- Optimize for **learning depth over shipping speed** — lean into the gnarly parts
  (especially Kotlin scripting) instead of routing around them.
- Explaining tradeoffs and asking clarifying questions is encouraged.

## What this is

**kmake** — a `make`-like CLI task runner where tasks are defined in a type-safe Kotlin
DSL loaded from a `.kts` file. Greenfield.

## Plan & decisions

- **`BACKLOG.md`** — the source of truth: ordered backlog with progress checkboxes plus
  the locked-decisions table. Keep it updated as steps complete.
- Deeper rationale (feedback that shaped the plan): `~/.claude/plans/see-previous-message-typed-hearth.md`.

## Environment

- macOS, Apple Silicon (`aarch64`).
- Toolchain installed on the **host via Homebrew**: Temurin **JDK 21.0.11 LTS**,
  **Kotlin 2.3.x** (CLI 2.3.21), **Gradle 9.5.1**. `JAVA_HOME` via `/usr/libexec/java_home -v 21`.
- Editor: **Neovim** (custom config) with JetBrains **`kotlin-lsp`** (installed via Mason) + `ktlint`.
  - ⚠️ The fwcd `kotlin-language-server` does **not** work with Gradle 9 / Kotlin 2.3 (crashes on init / can't resolve classpath). Use JetBrains `kotlin-lsp`. Don't go back.

## Commands

- Run a plain Kotlin script: `kotlinc -script spike/hello.kts` (first run is slow — Kotlin
  compiler cold start; this is the cold-start UX wart we care about).
- Gradle wrapper / build / test / run commands: **to be added once the project is
  scaffolded** (Backlog step 1).

## Conventions / gotchas

- Kotlin **scripting library versions must equal the Kotlin compiler version** (2.3.x) —
  skew here is the top cause of cryptic scripting failures.
- Task actions are typed `suspend () -> Unit`.
- **Don't commit unless the user explicitly asks.**
