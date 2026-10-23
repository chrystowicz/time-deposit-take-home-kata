package org.ikigaidigital.application

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.Withdrawal

/** A read-model pairing a time deposit with its own withdrawals, for GetAllTimeDepositsUseCase. */
data class TimeDepositWithWithdrawals(
    val timeDeposit: TimeDeposit,
    val withdrawals: List<Withdrawal>,
)
