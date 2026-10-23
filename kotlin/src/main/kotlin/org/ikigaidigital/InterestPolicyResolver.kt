package org.ikigaidigital

/** Resolves the [InterestPolicy] that applies to a given plan type. */
interface InterestPolicyResolver {
    fun resolve(planType: String): InterestPolicy
}

/**
 * Resolves plan types by exact (case-sensitive) string match against a fixed
 * registry of known plans, falling back to [NoInterestPolicy] for anything
 * else. Adding a new plan type means adding to [policiesByPlanType] here,
 * not touching [TimeDepositCalculator].
 */
class PlanTypeInterestPolicyResolver(
    private val policiesByPlanType: Map<String, InterestPolicy> =
        mapOf(
            "basic" to BasicInterestPolicy,
            "student" to StudentInterestPolicy,
            "premium" to PremiumInterestPolicy,
        ),
) : InterestPolicyResolver {
    override fun resolve(planType: String): InterestPolicy = policiesByPlanType[planType] ?: NoInterestPolicy
}
