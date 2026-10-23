package org.ikigaidigital.adapters.web

import org.ikigaidigital.adapters.web.generated.TimeDepositsApi
import org.ikigaidigital.adapters.web.generated.model.TimeDepositResponse
import org.ikigaidigital.core.deposit.GetAllTimeDepositsUseCase
import org.ikigaidigital.core.deposit.UpdateTimeDepositBalancesUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TimeDepositController(
    private val updateTimeDepositBalancesUseCase: UpdateTimeDepositBalancesUseCase,
    private val getAllTimeDepositsUseCase: GetAllTimeDepositsUseCase,
) : TimeDepositsApi {
    override fun updateBalances(): ResponseEntity<Unit> {
        updateTimeDepositBalancesUseCase.updateAllBalances()
        return ResponseEntity.noContent().build()
    }

    override fun getAllTimeDeposits(): ResponseEntity<List<TimeDepositResponse>> {
        val response = getAllTimeDepositsUseCase.getAll().map { it.toResponse() }
        return ResponseEntity.ok(response)
    }
}
