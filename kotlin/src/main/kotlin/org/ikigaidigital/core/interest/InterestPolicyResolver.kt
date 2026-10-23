package org.ikigaidigital.core.interest

class InterestPolicyResolver(
    private val policiesByPlanType: Map<String, InterestPolicy> =
        mapOf(
            "basic" to BasicInterestPolicy,
            "student" to StudentInterestPolicy,
            "premium" to PremiumInterestPolicy,
        ),
) {
    fun resolve(planType: String): InterestPolicy = policiesByPlanType[planType] ?: NoInterestPolicy
}
