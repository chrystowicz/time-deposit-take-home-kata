package org.ikigaidigital.adapters.persistence

import org.ikigaidigital.Withdrawal

/** The only place that translates between WithdrawalEntity and Withdrawal. */
fun WithdrawalEntity.toDomain(): Withdrawal =
    Withdrawal(
        id = id,
        timeDepositId = timeDepositId,
        amount = amount.toDouble(),
        date = date,
    )
