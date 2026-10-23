package org.ikigaidigital.adapters.web

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.Withdrawal
import org.ikigaidigital.application.GetAllTimeDepositsUseCase
import org.ikigaidigital.application.TimeDepositWithWithdrawals
import org.ikigaidigital.application.UpdateTimeDepositBalancesUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

/**
 * A web-layer slice test: use cases are mocked, so this only verifies the
 * HTTP contract (method, path, status code, body shape) and that the
 * controller actually delegates to its use case(s). Each use case's own
 * orchestration is tested by its own unit test.
 */
@WebMvcTest(TimeDepositController::class)
class TimeDepositControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var updateTimeDepositBalancesUseCase: UpdateTimeDepositBalancesUseCase

    @MockitoBean
    private lateinit var getAllTimeDepositsUseCase: GetAllTimeDepositsUseCase

    @Test
    fun `POST to the balance-updates endpoint triggers the use case and returns 204`() {
        mockMvc
            .perform(post("/api/v1/time-deposits/balance-updates").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)

        verify(updateTimeDepositBalancesUseCase).updateAllBalances()
    }

    @Test
    fun `GET to time-deposits returns every deposit with its withdrawals as JSON`() {
        val deposit = TimeDeposit(1, "basic", 1000.83, 31)
        val withdrawal = Withdrawal(1, timeDepositId = 1, amount = 100.50, date = LocalDate.of(2024, 1, 15))
        whenever(getAllTimeDepositsUseCase.getAll())
            .thenReturn(listOf(TimeDepositWithWithdrawals(deposit, listOf(withdrawal))))

        mockMvc
            .perform(get("/api/v1/time-deposits"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].planType").value("basic"))
            .andExpect(jsonPath("$[0].balance").value(1000.83))
            .andExpect(jsonPath("$[0].days").value(31))
            .andExpect(jsonPath("$[0].withdrawals[0].amount").value(100.50))
            .andExpect(jsonPath("$[0].withdrawals[0].date").value("2024-01-15"))
    }

    @Test
    fun `GET to time-deposits returns an empty array when there are no deposits`() {
        whenever(getAllTimeDepositsUseCase.getAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/api/v1/time-deposits"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }
}
