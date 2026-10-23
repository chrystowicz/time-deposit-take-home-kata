package org.ikigaidigital.adapters.persistence

import org.ikigaidigital.TimeDeposit
import java.math.BigDecimal
import java.math.RoundingMode

fun TimeDepositEntity.toDomain(): TimeDeposit =
    TimeDeposit(
        id = id,
        planType = planType,
        balance = balance.toDouble(),
        days = days,
    )

fun TimeDeposit.toEntity(): TimeDepositEntity =
    TimeDepositEntity(
        id = id,
        planType = planType,
        days = days,
        balance = BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_UP),
    )
