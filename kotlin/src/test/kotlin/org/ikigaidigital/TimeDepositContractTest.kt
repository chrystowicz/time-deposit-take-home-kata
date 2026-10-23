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
    fun `the default production wiring (real policy resolver and rounder) computes correct balances`() {
        // TimeDepositCalculatorTest mocks InterestPolicyResolver/CurrencyRounder to test
        // orchestration in isolation; this pins down that the real default collaborators
        // are wired together correctly end to end.
        val calculator = TimeDepositCalculator()
        val basic = TimeDeposit(1, "basic", 1000.00, 31)
        val student = TimeDeposit(2, "student", 1000.00, 31)
        val premium = TimeDeposit(3, "premium", 1000.00, 46)

        calculator.updateBalance(listOf(basic, student, premium))

        assertThat(basic.balance).isEqualTo(1000.83)
        assertThat(student.balance).isEqualTo(1002.50)
        assertThat(premium.balance).isEqualTo(1004.17)
    }
}