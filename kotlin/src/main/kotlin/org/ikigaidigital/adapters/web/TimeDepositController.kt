package org.ikigaidigital.adapters.web

import org.ikigaidigital.application.UpdateTimeDepositBalancesUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/time-deposits")
class TimeDepositController(
    private val updateTimeDepositBalancesUseCase: UpdateTimeDepositBalancesUseCase,
) {
    @PostMapping("/balance-updates")
    fun updateBalances(): ResponseEntity<Void> {
        updateTimeDepositBalancesUseCase.updateAllBalances()
        return ResponseEntity.noContent().build()
    }
}
