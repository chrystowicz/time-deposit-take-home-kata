package org.ikigaidigital.application

import org.ikigaidigital.TimeDepositCalculator

/** Applies this period's interest to every time deposit and persists the result. */
interface UpdateTimeDepositBalancesUseCase {
    fun updateAllBalances()
}

class DefaultUpdateTimeDepositBalancesUseCase(
    private val repository: TimeDepositRepository,
    private val calculator: TimeDepositCalculator,
) : UpdateTimeDepositBalancesUseCase {
    override fun updateAllBalances() {
        val deposits = repository.findAll()
        calculator.updateBalance(deposits)
        repository.saveAll(deposits)
    }
}
