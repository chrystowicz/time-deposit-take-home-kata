package org.ikigaidigital.adapters.web

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.adapters.AbstractPostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

/**
 * The real vertical slice, end to end: a real HTTP call, against the real
 * controller/use case/repository wiring, against a real Postgres container --
 * seeds raw rows directly via JdbcTemplate, calls the actual endpoint over
 * HTTP, then reads the rows back to prove the balances genuinely changed in
 * the database. This is the single test that proves README Requirement 1's
 * "update the balances of all time deposits in the database" actually works.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateBalancesEndpointAcceptanceTest : AbstractPostgresIntegrationTest() {
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
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `POST to the balance-updates endpoint applies interest to every deposit in the database`() {
        jdbcTemplate.update("DELETE FROM time_deposits")
        jdbcTemplate.update(
            "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (?, ?, ?, ?)",
            1,
            "basic",
            31,
            1000.00,
        )
        jdbcTemplate.update(
            "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (?, ?, ?, ?)",
            2,
            "premium",
            30,
            5000.00,
        )

        val response = restTemplate.postForEntity("/api/v1/time-deposits/balance-updates", null, Void::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        val basicBalance =
            jdbcTemplate.queryForObject(
                "SELECT balance FROM time_deposits WHERE id = ?",
                java.math.BigDecimal::class.java,
                1,
            )
        val premiumBalance =
            jdbcTemplate.queryForObject(
                "SELECT balance FROM time_deposits WHERE id = ?",
                java.math.BigDecimal::class.java,
                2,
            )
        // basic, 31 days: 1000 * 0.01 / 12 = 0.8333... -> rounds to 0.83
        assertThat(basicBalance).isEqualByComparingTo("1000.83")
        // premium, 30 days: not yet earning interest (starts after day 45)
        assertThat(premiumBalance).isEqualByComparingTo("5000.00")
    }
}
