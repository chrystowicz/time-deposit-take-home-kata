package org.ikigaidigital

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class InterestPolicyTest {

    @ParameterizedTest(name = "basic, {0} days: balance {1} -> raw interest {2}")
    @CsvSource(
        "30, 1000.00, 0.0",
        "31, 1000.00, 0.8333333333333334",
        "365, 1000.00, 0.8333333333333334",
    )
    fun `BasicInterestPolicy pays 1 percent annual interest, monthly, once older than 30 days`(
        days: Int,
        balance: Double,
        expectedRawInterest: Double,
    ) {
        val deposit = TimeDeposit(1, "basic", balance, days)

        assertThat(BasicInterestPolicy.monthlyInterest(deposit)).isEqualTo(expectedRawInterest)
    }

    @ParameterizedTest(name = "student, {0} days: balance {1} -> raw interest {2}")
    @CsvSource(
        "30, 1000.00, 0.0",
        "31, 1000.00, 2.5",
        "365, 1000.00, 2.5",
        "366, 1000.00, 0.0",
    )
    fun `StudentInterestPolicy pays 3 percent annual interest, monthly, only while under 366 days old`(
        days: Int,
        balance: Double,
        expectedRawInterest: Double,
    ) {
        val deposit = TimeDeposit(1, "student", balance, days)

        assertThat(StudentInterestPolicy.monthlyInterest(deposit)).isEqualTo(expectedRawInterest)
    }

    @ParameterizedTest(name = "premium, {0} days: balance {1} -> raw interest {2}")
    @CsvSource(
        "30, 1000.00, 0.0",
        "45, 1000.00, 0.0",
        "46, 1000.00, 4.166666666666667",
    )
    fun `PremiumInterestPolicy pays 5 percent annual interest, monthly, once older than 45 days`(
        days: Int,
        balance: Double,
        expectedRawInterest: Double,
    ) {
        val deposit = TimeDeposit(1, "premium", balance, days)

        assertThat(PremiumInterestPolicy.monthlyInterest(deposit)).isEqualTo(expectedRawInterest)
    }

    @ParameterizedTest(name = "{0} plan, {1} days")
    @CsvSource(
        "unknown, 1000",
        "Basic, 1000",
        "STUDENT, 1000",
    )
    fun `NoInterestPolicy pays nothing, regardless of balance or days`(planType: String, days: Int) {
        val deposit = TimeDeposit(1, planType, 1_000_000.00, days)

        assertThat(NoInterestPolicy.monthlyInterest(deposit)).isEqualTo(0.0)
    }
}
