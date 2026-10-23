package org.ikigaidigital.adapters.web

import org.ikigaidigital.application.UpdateTimeDepositBalancesUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * A web-layer slice test: the use case is mocked, so this only verifies the
 * HTTP contract (method, path, status code) and that the controller actually
 * delegates to the use case. The use case's own orchestration is tested by
 * UpdateTimeDepositBalancesUseCaseTest.
 */
@WebMvcTest(TimeDepositController::class)
class TimeDepositControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var updateTimeDepositBalancesUseCase: UpdateTimeDepositBalancesUseCase

    @Test
    fun `POST to the balance-updates endpoint triggers the use case and returns 204`() {
        mockMvc
            .perform(post("/api/v1/time-deposits/balance-updates").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)

        verify(updateTimeDepositBalancesUseCase).updateAllBalances()
    }
}
