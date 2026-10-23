package org.ikigaidigital.adapters.web

import org.ikigaidigital.Withdrawal
import org.ikigaidigital.adapters.web.generated.model.TimeDepositResponse
import org.ikigaidigital.adapters.web.generated.model.WithdrawalResponse
import org.ikigaidigital.application.TimeDepositWithWithdrawals
import java.math.BigDecimal
import java.math.RoundingMode

/** The only place that translates the application read-model into the generated API response shape. */
fun TimeDepositWithWithdrawals.toResponse(): TimeDepositResponse =
    TimeDepositResponse(
        id = timeDeposit.id,
        planType = timeDeposit.planType,
        balance = timeDeposit.balance.toMoney(),
        days = timeDeposit.days,
        withdrawals = withdrawals.map { it.toResponse() },
    )

fun Withdrawal.toResponse(): WithdrawalResponse =
    WithdrawalResponse(
        amount = amount.toMoney(),
        date = date,
    )

// BigDecimal.valueOf(Double), not the BigDecimal(Double) constructor, then
// setScale(2, HALF_UP): same two-step normalization TimeDepositMapper.toEntity
// uses, so a whole-number value (e.g. 5000.0) serializes as "5000.00" rather
// than "5000.0" or the double's exact (and misleading) binary expansion.
private fun Double.toMoney(): BigDecimal = BigDecimal.valueOf(this).setScale(2, RoundingMode.HALF_UP)
