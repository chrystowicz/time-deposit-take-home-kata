package org.ikigaidigital.adapters.persistence

import org.ikigaidigital.TimeDeposit
import java.math.BigDecimal
import java.math.RoundingMode

/** The only place that translates between TimeDepositEntity and TimeDeposit. */
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
        // BigDecimal.valueOf(Double), NOT the BigDecimal(Double) constructor
        // (what Double.toBigDecimal() calls): the constructor uses the
        // double's exact binary value (1000.83 -> 1000.8300000000000409...),
        // while valueOf goes through Double.toString() and produces the
        // decimal value actually intended -- see CurrencyRounder's own
        // comment for the same footgun elsewhere in this codebase.
        //
        // setScale(2, HALF_UP): the time_deposits.balance column is
        // DECIMAL(19,2), so a row read back via rs.getBigDecimal is always
        // scale 2. Without normalizing here too, a whole-number or
        // one-decimal balance (e.g. 1000.0 -> BigDecimal("1000"), scale 0)
        // would produce a TimeDepositEntity that's numerically correct but
        // NOT equal (BigDecimal.equals is scale-sensitive) to the entity a
        // repository round-trip would read back for the same value.
        balance = BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_UP),
    )
