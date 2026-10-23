package org.ikigaidigital

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.core.deposit.TimeDepositCalculator
import org.ikigaidigital.core.interest.InterestPolicyResolver
import org.ikigaidigital.core.utils.HalfUpCurrencyRounder
import org.junit.jupiter.api.Test

class TimeDepositContractTest {
    @Test
    fun `public API of TimeDeposit and updateBalance stays unchanged`() {
        // Compiles only while the constructor and updateBalance keep their current signatures.
        val deposit = TimeDeposit(1, "basic", 1234567.00, 45)
        val calculator =
            TimeDepositCalculator(
                InterestPolicyResolver(),
                HalfUpCurrencyRounder(),
            )
        calculator.updateBalance(listOf(deposit))
    }

    @Test
    fun `id is exposed as provided so deposits can be identified by callers`() {
        val deposit = TimeDeposit(42, "basic", 1000.00, 31)

        assertThat(deposit.id).isEqualTo(42)
    }
}
