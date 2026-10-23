package org.ikigaidigital

class TimeDepositCalculator(
    private val policyResolver: InterestPolicyResolver = PlanTypeInterestPolicyResolver(),
    private val currencyRounder: CurrencyRounder = HalfUpCurrencyRounder(),
) {
    fun updateBalance(xs: List<TimeDeposit>) {
        for (deposit in xs) {
            val policy = policyResolver.resolve(deposit.planType)
            val interest = policy.monthlyInterest(deposit)
            deposit.balance += currencyRounder.round(interest)
        }
    }
}
