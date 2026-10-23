package org.ikigaidigital.core.deposit

import java.time.LocalDate

data class Withdrawal(
    val id: Int,
    val timeDepositId: Int,
    val amount: Double, // left this as Double for compatibility with TimeDeposit, but it's wrong
    val date: LocalDate, // date to match README requirements, but strongly suggest to use Instant
)
