package org.ikigaidigital.adapters.persistence

import java.math.BigDecimal
import java.time.LocalDate

data class WithdrawalEntity(
    val id: Int,
    val timeDepositId: Int,
    val amount: BigDecimal,
    val date: LocalDate, // date to match README requirements, but strongly suggest to use Instant
)
