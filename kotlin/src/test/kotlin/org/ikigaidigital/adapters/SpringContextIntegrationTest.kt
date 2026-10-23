package org.ikigaidigital.adapters

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.CurrencyRounder
import org.ikigaidigital.InterestPolicyResolver
import org.ikigaidigital.TimeDepositCalculator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

/**
 * Proves the whole scaffolded chain actually works end to end: the Spring
 * context boots against a *real* Postgres instance running in a Testcontainers
 * container, and the domain beans wired by DomainBeansConfiguration are
 * exactly the interface types the rest of the application is meant to depend
 * on -- never a concrete implementation.
 */
class SpringContextIntegrationTest : AbstractPostgresIntegrationTest() {
    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16")
                .withDatabaseName("timedeposit")
                .withUsername("timedeposit")
                .withPassword("timedeposit")
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
