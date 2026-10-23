package org.ikigaidigital

import java.time.LocalDate

/** A single withdrawal recorded against a time deposit. New type -- does not touch TimeDeposit.kt. */
data class Withdrawal(
    val id: Int,
    val timeDepositId: Int,
    val amount: Double,
    val date: LocalDate,
)
