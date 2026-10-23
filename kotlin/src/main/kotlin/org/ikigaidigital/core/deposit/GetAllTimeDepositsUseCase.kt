package org.ikigaidigital.core.deposit

class GetAllTimeDepositsUseCase(
    private val timeDepositRepository: TimeDepositRepository,
    private val withdrawalRepository: WithdrawalRepository,
) {
    fun getAll(): List<TimeDepositWithWithdrawals> {
        val withdrawalsByTimeDepositId = withdrawalRepository.findAll().groupBy { it.timeDepositId }

        return timeDepositRepository.findAll().map { deposit ->
            TimeDepositWithWithdrawals(deposit, withdrawalsByTimeDepositId[deposit.id].orEmpty())
        }
    }
}
