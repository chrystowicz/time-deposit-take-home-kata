package org.ikigaidigital.adapters.persistence

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.Withdrawal
import org.ikigaidigital.adapters.AbstractPostgresIntegrationTest
import org.ikigaidigital.application.WithdrawalRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import java.time.LocalDate

/**
 * Round-trips real rows through a real Postgres instance (Testcontainers),
 * proving JdbcWithdrawalRepository's SQL actually works -- same pattern as
 * JdbcTimeDepositRepositoryTest.
 */
class JdbcWithdrawalRepositoryTest : AbstractPostgresIntegrationTest() {
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
    private lateinit var repository: WithdrawalRepository

    @AfterEach
    fun cleanUp() {
        jdbcTemplate.update("DELETE FROM withdrawals")
        jdbcTemplate.update("DELETE FROM time_deposits")
    }

    @Test
    fun `findAll returns every withdrawal row in the table`() {
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
            100.50,
            LocalDate.of(2024, 1, 15),
        )
        jdbcTemplate.update(
            "INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES (?, ?, ?, ?)",
            2,
            1,
            50.00,
            LocalDate.of(2024, 2, 1),
        )

        val withdrawals = repository.findAll()

        assertThat(withdrawals).containsExactlyInAnyOrder(
            Withdrawal(1, 1, 100.50, LocalDate.of(2024, 1, 15)),
            Withdrawal(2, 1, 50.00, LocalDate.of(2024, 2, 1)),
        )
    }
}
