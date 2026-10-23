package org.ikigaidigital.core.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CurrencyRounderTest {
    private val rounder: CurrencyRounder = HalfUpCurrencyRounder()

    @ParameterizedTest(name = "{0} rounds to {1}")
    @CsvSource(
        "0.8333333333333334, 0.83",
        "4.166666666666667, 4.17",
        "2.5, 2.50",
        "0.0, 0.00",
        // the double's exact binary value here is 1.0050000000000001..., fractionally
        // above the 1.005 midpoint, so HALF_UP rounds it to 1.01.
        "1.0050000000000001, 1.01",
    )
    fun `rounds to two decimal places using half-up rounding`(
        raw: Double,
        expected: Double,
    ) {
        assertThat(rounder.round(raw)).isEqualTo(expected)
    }
}
