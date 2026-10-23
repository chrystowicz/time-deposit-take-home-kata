package org.ikigaidigital

/**
 * Computes the monthly interest earned by a single time deposit, in whole
 * currency units, unrounded. Each plan type has its own policy; an unknown
 * plan type earns no interest (see [NoInterestPolicy]).
 */
interface InterestPolicy {
    fun monthlyInterest(deposit: TimeDeposit): Double
}

/** 1% annual interest, monthly, once the deposit is older than 30 days. */
object BasicInterestPolicy : InterestPolicy {
    override fun monthlyInterest(deposit: TimeDeposit): Double = if (deposit.days > 30) deposit.balance * 0.01 / 12 else 0.0
}

/** 3% annual interest, monthly, only while the deposit is between 31 and 365 days old. */
object StudentInterestPolicy : InterestPolicy {
    override fun monthlyInterest(deposit: TimeDeposit): Double =
        if (deposit.days > 30 && deposit.days < 366) deposit.balance * 0.03 / 12 else 0.0
}

/** 5% annual interest, monthly, once the deposit is older than 45 days. */
object PremiumInterestPolicy : InterestPolicy {
    override fun monthlyInterest(deposit: TimeDeposit): Double = if (deposit.days > 45) deposit.balance * 0.05 / 12 else 0.0
}

/** Fallback for any plan type with no matching policy: earns no interest. */
object NoInterestPolicy : InterestPolicy {
    override fun monthlyInterest(deposit: TimeDeposit): Double = 0.0
}
