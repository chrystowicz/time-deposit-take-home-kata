package org.ikigaidigital.adapters

import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.DockerClientFactory
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Skips the class entirely (rather than letting it fail) when a real, usable
 * Docker daemon isn't reachable -- e.g. on a machine where Docker Desktop's
 * API-version negotiation is broken for the docker-java client that
 * Testcontainers relies on. That's a local environment problem, not a code
 * defect, and shouldn't block running the rest of the suite (mutation testing
 * in particular requires an all-green/skipped run to compute coverage).
 *
 * This has to be a JUnit [ExecutionCondition] rather than an `@BeforeAll`
 * method: conditions are evaluated before any extension's `beforeAll`
 * callback runs, including the one [Testcontainers] registers to actually
 * start the container -- a plain `@BeforeAll` with `Assumptions.assumeTrue`
 * runs too late to prevent that container-start attempt.
 *
 * A silent skip is fine for a developer's laptop, but on CI it would
 * otherwise disappear into "skipped=N" in the JUnit XML forever, hiding a
 * real regression (Docker genuinely broken in the pipeline) behind what
 * looks like the usual local quirk. Set CI=true to turn that into a loud
 * failure instead.
 */
class DockerAvailableCondition : ExecutionCondition {
    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val dockerAvailable =
            try {
                DockerClientFactory.instance().isDockerAvailable
            } catch (_: Exception) {
                false
            }
        if (dockerAvailable) {
            return ConditionEvaluationResult.enabled("Docker is available")
        }
        val runningInCi = System.getenv("CI")?.toBoolean() == true
        check(!runningInCi) {
            "Docker is not available/usable, but CI=true -- failing loudly instead of " +
                "silently skipping a Testcontainers-backed test, since a skip here would " +
                "hide a real pipeline regression."
        }
        println(
            "WARNING: Docker is not available/usable on this machine -- " +
                "skipping ${context.displayName} (Testcontainers-backed).",
        )
        return ConditionEvaluationResult.disabled(
            "Docker is not available/usable on this machine -- skipping Testcontainers-backed test",
        )
    }
}

/**
 * Shared base for any test that needs a real Spring context wired against a
 * real, containerized Postgres instance. Subclasses declare their own
 * `@Container`-annotated Postgres field (see SpringContextIntegrationTest or
 * JdbcTimeDepositRepositoryTest for the pattern) -- deliberately NOT shared
 * here: a container field shared across unrelated test classes, combined
 * with Spring's test-context caching, causes one class's teardown to stop
 * the container out from under the next class, which then silently
 * reconnects to a freshly-restarted, empty database on a different port.
 * One container per class costs a little startup time but keeps each test
 * class's database state fully isolated and avoids that trap entirely.
 */
@ExtendWith(DockerAvailableCondition::class)
@Testcontainers
@SpringBootTest
abstract class AbstractPostgresIntegrationTest
