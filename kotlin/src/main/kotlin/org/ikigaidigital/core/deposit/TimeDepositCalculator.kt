package org.ikigaidigital.core.deposit

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.core.interest.InterestPolicyResolver
import org.ikigaidigital.core.utils.CurrencyRounder

class TimeDepositCalculator(
    private val policyResolver: InterestPolicyResolver,
    private val currencyRounder: CurrencyRounder,
) {
    fun updateBalance(xs: List<TimeDeposit>) {
        for (deposit in xs) {
            val policy = policyResolver.resolve(deposit.planType)
            val interest = policy.monthlyInterest(deposit)
            deposit.balance += currencyRounder.round(interest)
        }
    }
}
