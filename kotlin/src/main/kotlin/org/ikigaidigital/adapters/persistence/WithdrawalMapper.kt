package org.ikigaidigital.adapters.persistence

import org.ikigaidigital.core.deposit.Withdrawal

fun WithdrawalEntity.toDomain(): Withdrawal =
    Withdrawal(
        id = id,
        timeDepositId = timeDepositId,
        amount = amount.toDouble(),
        date = date,
    )
