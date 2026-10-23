package org.ikigaidigital

import java.math.BigDecimal
import java.math.RoundingMode

class TimeDepositCalculator {

    companion object {
        private val INTEREST_POLICIES: Map<String, InterestPolicy> = mapOf(
            "basic" to BasicInterestPolicy,
            "student" to StudentInterestPolicy,
            "premium" to PremiumInterestPolicy,
        )
    }

    fun updateBalance(xs: List<TimeDeposit>) {
        for (deposit in xs) {
            val policy = INTEREST_POLICIES[deposit.planType] ?: NoInterestPolicy
            val interest = policy.monthlyInterest(deposit)
            deposit.balance += roundToCurrency(interest)
        }
    }

    private fun roundToCurrency(amount: Double): Double =
        BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).toDouble()
}
