# Project: time-despot-take-home-kata
Kotlin 2.x, Spring Boot 3, Gradle Kotlin DSL, JUnit 5, Testcontainers.

## Commands
- Build: ./gradlew build
- Test: ./gradlew test
- Lint: ./gradlew ktlintCheck

## Rules
- TDD: write a failing test first, then the implementation.
- No new dependencies without asking me.
- Keep domain logic free of framework annotations (hexagonal layout: domain / application / adapters).
- Never edit generated files or anything under build/.
- After each change, run the tests and report the result honestly. If a test fails, say so.
- Small commits with conventional-commit messages.

## Hard constraints
- `TimeDeposit.kt` is a shared class used by other consumers. Do not introduce breaking changes:
    - do not remove, rename or change the type of any public field, property, constructor or method
    - do not change constructor parameter order
- Do not change the signature of `updateBalance` inside `TimeDepositCalculator.kt` (name, parameter types, return type, visibility).
- Refactor *around* these: extract new classes or strategies and call them from inside `updateBalance`.
- If a task seems to require breaking either rule, stop and ask me instead of working around it.