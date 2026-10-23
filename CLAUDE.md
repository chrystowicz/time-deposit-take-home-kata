# Project: time-despot-take-home-kata
Kotlin 2.x, Spring Boot 3, Gradle Kotlin DSL, JUnit 5, Testcontainers.

## Commands
- Build: ./gradlew build
- Test: ./gradlew test
- Lint: ./gradlew ktlintCheck
- PIT: ./gradlew pitest

## Rules
- TDD: write a failing test first, then the implementation.
- If an honest test is impractical, do not write a bad one.
- Keep the business logic in a pure function.
- Make Operations idempotent - running it twice, or running it after a crash halfway through, ends in the same state.
- Follow SOLID principles, embrace small, focused unit tests, and prioritize testing business logic over dependencies. Use mocks if needed.
- No new dependencies without asking me.
- Keep domain logic free of framework annotations (hexagonal layout: domain / application / adapters).
- Never edit generated files or anything under build/.
- After each change, run the tests and report the result honestly. If a test fails, say so.
- Small commits with conventional-commit messages.
- Prioritize improving the local development environment before introducing additional checks for issues such as third-party dependency updates

## Hard constraints
- `TimeDeposit.kt` is a shared class used by other consumers. Do not introduce breaking changes:
    - do not remove, rename or change the type of any public field, property, constructor or method
    - do not change constructor parameter order
- Do not change the signature of `updateBalance` inside `TimeDepositCalculator.kt` (name, parameter types, return type, visibility).
- Refactor *around* these: extract new classes or strategies and call them from inside `updateBalance`.
- If a task seems to require breaking either rule, stop and ask me instead of working around it.

### Technical constraints

## Rest APIs 
- Start REST APIs with version, e.g. /v1/...
- Embrace idempotency whenever possible