package org.ikigaidigital.adapters

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.CurrencyRounder
import org.ikigaidigital.InterestPolicyResolver
import org.ikigaidigital.TimeDepositCalculator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
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
 * otherwise disappear into "skipped=2" in the JUnit XML forever, hiding a
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
                "silently skipping SpringContextIntegrationTest, since a skip here would " +
                "hide a real pipeline regression."
        }
        println(
            "WARNING: Docker is not available/usable on this machine -- " +
                "skipping SpringContextIntegrationTest (Testcontainers-backed).",
        )
        return ConditionEvaluationResult.disabled(
            "Docker is not available/usable on this machine -- skipping Testcontainers-backed test",
        )
    }
}

/**
 * Proves the whole scaffolded chain actually works end to end: the Spring
 * context boots against a *real* Postgres instance running in a Testcontainers
 * container, and the domain beans wired by DomainBeansConfiguration are
 * exactly the interface types the rest of the application is meant to depend
 * on -- never a concrete implementation.
 */
@ExtendWith(DockerAvailableCondition::class)
@Testcontainers
@SpringBootTest
class SpringContextIntegrationTest {
    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16")
                .withDatabaseName("timedeposit")
                .withUsername("timedeposit")
                .withPassword("timedeposit")

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            // @ServiceConnection already wires spring.datasource.* from the
            // container; this is here only as a safety net should that
            // auto-configuration ever be bypassed.
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var interestPolicyResolver: InterestPolicyResolver

    @Autowired
    private lateinit var currencyRounder: CurrencyRounder

    @Autowired
    private lateinit var timeDepositCalculator: TimeDepositCalculator

    @Test
    fun `the Spring context boots against a real containerized Postgres instance`() {
        val result: Int? = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)

        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `domain beans are reachable via dependency injection in the full application context`() {
        // Which concrete class backs each interface is already verified in
        // isolation by DomainBeansConfigurationTest; this only confirms the
        // beans are actually resolvable here, in the real, fully-booted app.
        assertThat(interestPolicyResolver).isNotNull()
        assertThat(currencyRounder).isNotNull()
        assertThat(timeDepositCalculator).isNotNull()
    }
}
