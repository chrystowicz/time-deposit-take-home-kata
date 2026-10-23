package org.ikigaidigital

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TimeDepositContractTest {
    @Test
    fun `public API of TimeDeposit and updateBalance stays unchanged`() {
        // Compiles only while the constructor and updateBalance keep their current signatures.
        val deposit = TimeDeposit(1, "basic", 1234567.00, 45)
        val calculator = TimeDepositCalculator()
        calculator.updateBalance(listOf(deposit))
    }

    @Test
    fun `id is exposed as provided so deposits can be identified by callers`() {
        val deposit = TimeDeposit(42, "basic", 1000.00, 31)

        assertThat(deposit.id).isEqualTo(42)
    }

    // End-to-end verification that TimeDepositCalculator()'s default collaborators
    // (PlanTypeInterestPolicyResolver, HalfUpCurrencyRounder) are wired together
    // correctly and produce the right balances now lives in
    // InterestCalculationAcceptanceTest, which covers every business-rule boundary
    // rather than just a single case per plan.
}
