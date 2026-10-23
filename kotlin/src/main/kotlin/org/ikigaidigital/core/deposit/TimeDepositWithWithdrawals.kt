package org.ikigaidigital.core.deposit

import org.ikigaidigital.TimeDeposit

data class TimeDepositWithWithdrawals(
    val timeDeposit: TimeDeposit,
    val withdrawals: List<Withdrawal>,
)
