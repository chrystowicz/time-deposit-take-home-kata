package org.ikigaidigital.adapters.web

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.adapters.AbstractPostgresIntegrationTest
import org.ikigaidigital.adapters.web.generated.model.TimeDepositResponse
import org.ikigaidigital.adapters.web.generated.model.WithdrawalResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import java.math.BigDecimal
import java.time.LocalDate

/**
 * The real vertical slice, end to end: seeds raw rows directly via
 * JdbcTemplate into both time_deposits and withdrawals, calls the actual
 * GET endpoint over HTTP against a real Postgres container, and checks the
 * response body -- proving README Requirement 1's "retrieve all time
 * deposits" (with the withdrawals schema field) actually works.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAllTimeDepositsEndpointAcceptanceTest : AbstractPostgresIntegrationTest() {
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
    fun `GET to time-deposits returns every deposit with its own withdrawals`() {
        jdbcTemplate.update("DELETE FROM withdrawals")
        jdbcTemplate.update("DELETE FROM time_deposits")
        jdbcTemplate.update(
            "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (?, ?, ?, ?)",
            1,
            "basic",
            31,
            1000.83,
        )
        jdbcTemplate.update(
            "INSERT INTO time_deposits (id, plan_type, days, balance) VALUES (?, ?, ?, ?)",
            2,
            "premium",
            46,
            5000.00,
        )
        jdbcTemplate.update(
            "INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES (?, ?, ?, ?)",
            1,
            1,
            100.50,
            LocalDate.of(2024, 1, 15),
        )

        val response =
            restTemplate.getForEntity("/api/v1/time-deposits", Array<TimeDepositResponse>::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!.toList()
        assertThat(body).containsExactlyInAnyOrder(
            TimeDepositResponse(
                id = 1,
                planType = "basic",
                balance = BigDecimal("1000.83"),
                days = 31,
                withdrawals =
                    listOf(
                        WithdrawalResponse(
                            amount = BigDecimal("100.50"),
                            date = LocalDate.of(2024, 1, 15),
                        ),
                    ),
            ),
            TimeDepositResponse(
                id = 2,
                planType = "premium",
                balance = BigDecimal("5000.00"),
                days = 46,
                withdrawals = emptyList(),
            ),
        )
    }
}
