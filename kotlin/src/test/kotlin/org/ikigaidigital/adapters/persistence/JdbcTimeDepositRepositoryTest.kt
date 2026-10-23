package org.ikigaidigital.adapters.persistence

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.adapters.AbstractPostgresIntegrationTest
import org.ikigaidigital.application.TimeDepositRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

/**
 * Round-trips real rows through a real Postgres instance (Testcontainers),
 * proving JdbcTimeDepositRepository's SQL actually works -- findAll reads
 * back exactly what saveAll wrote, including updates to existing rows.
 * Depends on the TimeDepositRepository port, not the concrete class, same as
 * every other consumer of this bean.
 */
class JdbcTimeDepositRepositoryTest : AbstractPostgresIntegrationTest() {
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
    private lateinit var repository: TimeDepositRepository

    @AfterEach
    fun cleanUp() {
        jdbcTemplate.update("DELETE FROM time_deposits")
    }

    @Test
    fun `findAll returns every row in the table`() {
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
            "student",
            31,
            2000.00,
        )

        val deposits = repository.findAll()

        assertThat(deposits).containsExactlyInAnyOrder(
            TimeDeposit(1, "basic", 1000.00, 31),
            TimeDeposit(2, "student", 2000.00, 31),
        )
    }

    @Test
    fun `saveAll persists updated balances back to their existing rows`() {
        jdbcTemplate.update(
            "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (?, ?, ?, ?)",
            1,
            "basic",
            31,
            1000.00,
        )
        val updated = TimeDeposit(1, "basic", 1000.83, 31)

        repository.saveAll(listOf(updated))

        val persistedBalance =
            jdbcTemplate.queryForObject(
                "SELECT balance FROM time_deposits WHERE id = ?",
                java.math.BigDecimal::class.java,
                1,
            )
        assertThat(persistedBalance).isEqualByComparingTo("1000.83")
    }
}
