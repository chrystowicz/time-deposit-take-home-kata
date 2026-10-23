package org.ikigaidigital.adapters.persistence

import java.math.BigDecimal
import java.time.LocalDate

/**
 * The persistence-shaped representation of a withdrawals row: amount as
 * BigDecimal, matching the DECIMAL(19,2) column (see schema.sql), mirroring
 * the same split TimeDepositEntity makes from the domain TimeDeposit.
 */
data class WithdrawalEntity(
    val id: Int,
    val timeDepositId: Int,
    val amount: BigDecimal,
    val date: LocalDate,
)
