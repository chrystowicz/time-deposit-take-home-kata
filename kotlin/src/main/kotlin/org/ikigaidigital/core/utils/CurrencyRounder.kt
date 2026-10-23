package org.ikigaidigital.core.utils

import java.math.BigDecimal
import java.math.RoundingMode

interface CurrencyRounder {
    fun round(amount: Double): Double
}

/** Rounds to 2 decimal places, half-up to make it backward compatible with the existing logic; the actual rounding is far more complicated and relies on currency */
class HalfUpCurrencyRounder : CurrencyRounder {
    override fun round(amount: Double): Double = BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).toDouble()
}
