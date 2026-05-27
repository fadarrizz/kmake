# kmake — Backlog

A make-like CLI task runner where tasks are defined in a type-safe Kotlin DSL
loaded from a `.kts` file. Learning project (Kotlin/JVM), optimizing for
**learning depth** over shipping speed.

Full rationale for this revised plan lives in the planning doc; this file is the
working checklist + locked decisions.

## Decisions locked

| Decision | Choice | Notes |
|----------|--------|-------|
| JDK | Temurin **21.0.11 LTS** | installed on host via Homebrew (`temurin@21`) |
| Kotlin | **2.3.x** (CLI 2.3.21) | pin the Gradle Kotlin plugin to match |
| Gradle | **9.5.1** | bundles Kotlin 2.3.20 for its own DSL |
| Env | host install (Homebrew) | Docker only later, as a distribution/CI artifact |
| Action type | `suspend () -> Unit` | run via `runBlocking`; keeps v2 parallelism a localized change |
| Dependencies | referenced **by name** (string) | validated at graph-resolution time; known type-safety tradeoff |
| Scripting | **custom script definition** | prototype with `kotlin-main-kts` host first, then migrate |
| Distribution | Gradle `application` plugin + `installDist` | gives a runnable `kmake` launcher |
| Task file | `tasks.kmake.kts` (TBD) + `-f` override | discover by walking up from cwd, like `make` finds `Makefile` |

## Backlog (ordered — each builds on the last)

- [x] **0. Toolchain** — JDK 21 + Kotlin + Gradle installed on host
  - [x] 0a. Prove a `.kts` runs at all (`kotlinc -script spike/hello.kts`)
  - [x] 0b. Scripting spike: a `.kts` that calls into our own compiled Kotlin (via `-cp`)
- [x] **1. Project scaffolding** — Gradle (Kotlin DSL) + wrapper, Clikt dep, source layout, runnable `main`. *(Logging lib deferred to step 5; subpackages created as we add code.)*
- [x] **2. Core domain model** — `Task(name, description, deps, action: suspend () -> Unit)` + `TaskRegistry` (with unit tests, TDD)
- [ ] **3. Minimal DSL** — `task(name) { description / dependsOn / action }` registers into the registry
- [ ] **4. Dependency graph + topo sort** — DFS (cycle detection + ordering in one pass); **unit tests**; name the actual cycle path (`a → b → c → a`) and unknown-dep errors
- [ ] **5. Sequential executor** — `runBlocking`, log start/end per task, fail fast on first error
- [ ] **6. Script loading** — replace hardcoded registry with the custom script definition (implicit receiver so `task(...)` needs no imports)
- [ ] **7. CLI entry point** — `kmake <task>` as a positional dispatch (task names are runtime values, not Clikt subcommands); `--list`, `--help`, `-f <file>`, file discovery
- [ ] **8. `sh(...)` helper** — `ProcessBuilder("sh", "-c", cmd)` (never `split(" ")`); stream stdout/stderr; non-zero exit → throw
- [ ] **9. Error handling + exit codes** — non-zero on failure; clear messages; stack traces only with `--verbose`
- [ ] **10. README + example `tasks.kmake.kts`** — realistic build/test/deploy tasks; dogfood the DSL on kmake itself

## Notes / gotchas

- **Scripting lib versions must equal the Kotlin compiler version** (the 2.3.x line).
  Version skew is the #1 cause of cryptic scripting failures.
- **Cold start is the main UX wart**: JVM startup + Kotlin script compilation can be
  1–2s+. Compile caching helps; GraalVM native-image is a tempting v2+ rabbit hole.
- **LSP help on `.kts` scripts is weak** (custom script definitions especially) — great
  autocomplete in `.kt` source, limited in `tasks.kmake.kts`. Same hard problem, in the editor.
- The string-based `dependsOn` undercuts the "type-safe" pitch — flag it; a task-handle
  reference design is a good v2 exploration.

## Out of scope (v2 — each is its own project)

Parallel execution · incremental builds (input/output tracking) · task parameters ·
plugin system · watch mode · remote caching · GraalVM native image.
