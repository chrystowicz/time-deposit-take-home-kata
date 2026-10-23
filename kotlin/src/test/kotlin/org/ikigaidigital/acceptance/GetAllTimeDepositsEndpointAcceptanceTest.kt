package org.ikigaidigital.acceptance

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.adapters.AbstractPostgresIntegrationTest
import org.ikigaidigital.adapters.TimeDepositApplication
import org.ikigaidigital.adapters.web.generated.model.TimeDepositResponse
import org.ikigaidigital.adapters.web.generated.model.WithdrawalResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.postgresql.PostgreSQLContainer
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest(classes = [TimeDepositApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAllTimeDepositsEndpointAcceptanceTest : AbstractPostgresIntegrationTest() {
    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer =
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
            restTemplate.getForEntity("/v1/time-deposits", Array<TimeDepositResponse>::class.java)

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
