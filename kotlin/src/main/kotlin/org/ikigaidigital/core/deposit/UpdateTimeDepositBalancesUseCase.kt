package org.ikigaidigital.core.deposit

class UpdateTimeDepositBalancesUseCase(
    private val repository: TimeDepositRepository,
    private val calculator: TimeDepositCalculator,
) {
    fun updateAllBalances() {
        val deposits = repository.findAll()
        calculator.updateBalance(deposits)
        repository.saveAll(deposits)
    }
}
