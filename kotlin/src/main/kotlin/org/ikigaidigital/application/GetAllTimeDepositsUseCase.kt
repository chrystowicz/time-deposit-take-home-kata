package org.ikigaidigital.application

/** Retrieves every time deposit together with its own withdrawals. */
interface GetAllTimeDepositsUseCase {
    fun getAll(): List<TimeDepositWithWithdrawals>
}

class DefaultGetAllTimeDepositsUseCase(
    private val timeDepositRepository: TimeDepositRepository,
    private val withdrawalRepository: WithdrawalRepository,
) : GetAllTimeDepositsUseCase {
    override fun getAll(): List<TimeDepositWithWithdrawals> {
        val withdrawalsByTimeDepositId = withdrawalRepository.findAll().groupBy { it.timeDepositId }
        return timeDepositRepository.findAll().map { deposit ->
            TimeDepositWithWithdrawals(deposit, withdrawalsByTimeDepositId[deposit.id].orEmpty())
        }
    }
}
