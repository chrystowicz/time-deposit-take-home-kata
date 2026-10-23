## Tools
- Claude Code 2.1.309, model Claude Sonnet 5 (`claude-sonnet-5`)
- No other AI assistants used on this codebase.

## Setup (reproduce)
1. `npm i -g @anthropic-ai/claude-code` (or the native installer).
2. Clone the repo, run `claude` in the root. `CLAUDE.md`, `.claude/agents`, `.claude/skills` and `.claude/settings.json`'s hooks load automatically — no extra setup step.

## Configuration
- `CLAUDE.md`: project rules the assistant follows on every turn -- TDD-first, keep business logic in pure functions, idempotent operations, hexagonal layering, the hard constraints freezing `TimeDeposit.kt`'s public shape and `TimeDepositCalculator.updateBalance`'s signature, and the gradle commands (`build`/`test`/`ktlintCheck`/`pitest`).
- `.claude/settings.json`: scopes what the assistant may run unprompted (`./gradlew test`/`build`, read-only git) and explicitly denies `git push` and reading `.env`; a `PostToolUse` hook runs `./gradlew ktlintFormat` after every edit so formatting never drifts.
- `.claude/agents/reviewer.md`, `.claude/agents/hardener.md`: subagents for an independent CLAUDE.md-aware review pass and for closing PIT mutation-testing gaps with new tests under `src/test` only.
- `.claude/skills/implement-feature`: a plan → failing-test → implementation → hardener → reviewer loop for picking up one requirement at a time.
- `.claude/skills/ub-mode`: a stricter work discipline invoked for this session's two features (the `withdrawals` table, the `GET /v1/time-deposits` endpoint) -- write the test red, make it pass with the smallest change, then run the full test suite, lint, and PIT mutation testing before calling the change done.

## What was AI-assisted, and why
Effectively the whole implementation was done with Claude Code driving the editor and shell directly, under the constraints above:
- The `withdrawals` table (`schema.sql`) and the `GET /v1/time-deposits` vertical slice (OpenAPI contract, domain `Withdrawal`/`TimeDepositWithWithdrawals`, the JDBC adapter, `GetAllTimeDepositsUseCase`, and the controller/response mapper) were all written test-first by the assistant, one layer at a time, with each test run red before its implementation.
- The assistant ran `./gradlew test`, `ktlintCheck`, and `pitest` after each change and reported results (including a mutation-testing gap on a Kotlin data class's synthetic getter, assessed as a non-issue rather than silently ignored).
- Design decisions it made on its own judgment: keeping `Withdrawal` as a new type instead of touching the frozen `TimeDeposit`, modeling the GET response as a read-model composition (`TimeDepositWithWithdrawals`) assembled in the application layer rather than in the controller, and choosing which fields the nested withdrawal JSON exposes (amount/date only, since the README's schema doesn't specify the nested shape).
- A human reviewed and approved the plan, the resulting diffs, and the commit messages before anything was pushed; the assistant did not fork the repository or send the submission email -- those remain manual steps.
- Why? Mostly because of a strict requirement in the README.md. However, the logic is so simple that the entire solution could have been written by hand
