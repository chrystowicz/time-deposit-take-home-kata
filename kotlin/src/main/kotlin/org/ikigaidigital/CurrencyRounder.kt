package org.ikigaidigital

import java.math.BigDecimal
import java.math.RoundingMode

/** Rounds a raw monetary amount down to the precision a balance is stored at. */
interface CurrencyRounder {
    fun round(amount: Double): Double
}

/** Rounds to 2 decimal places, half-up. */
class HalfUpCurrencyRounder : CurrencyRounder {
    override fun round(amount: Double): Double = BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).toDouble()
}
