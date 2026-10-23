package org.ikigaidigital.adapters.persistence

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.adapters.AbstractPostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import java.math.BigDecimal
import java.sql.Date

/**
 * Proves the `withdrawals` table from README.md Requirement 2 exists with
 * the required columns and enforces its foreign key to time_deposits --
 * nothing in the domain/application layers consumes this table yet, so this
 * pins the schema only.
 */
class WithdrawalsTableSchemaTest : AbstractPostgresIntegrationTest() {
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

    @Test
    fun `withdrawals row round-trips through the table with a valid time deposit reference`() {
        jdbcTemplate.update(
            "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (?, ?, ?, ?)",
            1,
            "basic",
            31,
            1000.00,
        )
        jdbcTemplate.update(
            "INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES (?, ?, ?, ?)",
            1,
            1,
            100.00,
            Date.valueOf("2024-01-15"),
        )

        val amount =
            jdbcTemplate.queryForObject(
                "SELECT amount FROM withdrawals WHERE id = ?",
                BigDecimal::class.java,
                1,
            )

        assertThat(amount).isEqualByComparingTo("100.00")

        jdbcTemplate.update("DELETE FROM withdrawals")
        jdbcTemplate.update("DELETE FROM time_deposits")
    }

    @Test
    fun `withdrawals rejects a time_deposit_id that does not reference an existing time deposit`() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy {
                jdbcTemplate.update(
                    "INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES (?, ?, ?, ?)",
                    99,
                    999999,
                    50.00,
                    Date.valueOf("2024-01-15"),
                )
            }.isInstanceOf(org.springframework.dao.DataIntegrityViolationException::class.java)
    }
}
