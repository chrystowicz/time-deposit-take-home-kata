package org.ikigaidigital.adapters.persistence

import java.math.BigDecimal

data class TimeDepositEntity(
    val id: Int,
    val planType: String,
    val days: Int,
    val balance: BigDecimal,
)
