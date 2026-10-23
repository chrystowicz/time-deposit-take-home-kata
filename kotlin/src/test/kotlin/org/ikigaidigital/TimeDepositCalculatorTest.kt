package org.ikigaidigital

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TimeDepositCalculatorTest {

    private val calculator = TimeDepositCalculator()

    @ParameterizedTest(name = "{0} plan, {1} days: balance {2} -> {3}")
    @CsvSource(
        // planType, days, startingBalance, expectedBalance
        // --- basic: flat 1% annual, monthly, once days > 30 ---
        "basic, 30, 1000.00, 1000.00",
        "basic, 31, 1000.00, 1000.83",
        "basic, 365, 1000.00, 1000.83",
        // --- student: 3% annual, monthly, only while 30 < days < 366 ---
        "student, 30, 1000.00, 1000.00",
        "student, 31, 1000.00, 1002.50",
        "student, 365, 1000.00, 1002.50",
        "student, 366, 1000.00, 1000.00",
        // --- premium: 5% annual, monthly, only once days > 45 ---
        "premium, 45, 1000.00, 1000.00",
        "premium, 46, 1000.00, 1004.17",
        // --- unknown plan type never earns interest ---
        "unknown, 1000, 1000.00, 1000.00",
        // --- plan type matching is case-sensitive: differently-cased names earn no interest ---
        "Basic, 31, 1000.00, 1000.00",
        "STUDENT, 31, 1000.00, 1000.00",
    )
    fun `updateBalance applies the correct monthly interest for each plan and day boundary`(
        planType: String,
        days: Int,
        startingBalance: Double,
        expectedBalance: Double,
    ) {
        val deposit = TimeDeposit(1, planType, startingBalance, days)

        calculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(expectedBalance)
    }

    @Test
    fun `interest landing on a rounding boundary rounds half-up, not half-even`() {
        // raw interest = 1206.00 * 0.01 / 12 -> the double's exact binary value is
        // 1.0050000000000001..., fractionally above the 1.005 midpoint, so both
        // HALF_UP and the more common HALF_EVEN round it to 1.01 here. This test
        // mainly pins down that balances are rounded to 2 decimals before being added.
        val deposit = TimeDeposit(1, "basic", 1206.00, 31)

        calculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(1207.01)
    }

    @Test
    fun `each deposit in the list is updated independently with its own plan and balance`() {
        val basic = TimeDeposit(1, "basic", 1000.00, 31)
        val student = TimeDeposit(2, "student", 2000.00, 31)
        val premium = TimeDeposit(3, "premium", 3000.00, 46)
        val tooYoung = TimeDeposit(4, "premium", 4000.00, 30)

        calculator.updateBalance(listOf(basic, student, premium, tooYoung))

        assertThat(basic.balance).isEqualTo(1000.83)
        assertThat(student.balance).isEqualTo(2005.00)
        assertThat(premium.balance).isEqualTo(3012.50)
        assertThat(tooYoung.balance).isEqualTo(4000.00)
    }

    @Test
    fun `an empty list of deposits is left untouched`() {
        calculator.updateBalance(emptyList())
    }
}
