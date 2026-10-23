package org.ikigaidigital.core.interest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class InterestPolicyResolverTest {
    private val resolver: InterestPolicyResolver = InterestPolicyResolver()

    @ParameterizedTest(name = "{0} resolves to {1}")
    @CsvSource(
        "basic, BasicInterestPolicy",
        "student, StudentInterestPolicy",
        "premium, PremiumInterestPolicy",
    )
    fun `resolves each known plan type to its own policy`(
        planType: String,
        expectedPolicyName: String,
    ) {
        val expectedPolicy =
            when (expectedPolicyName) {
                "BasicInterestPolicy" -> BasicInterestPolicy
                "StudentInterestPolicy" -> StudentInterestPolicy
                "PremiumInterestPolicy" -> PremiumInterestPolicy
                else -> error("unexpected policy name: $expectedPolicyName")
            }

        assertThat(resolver.resolve(planType)).isSameAs(expectedPolicy)
    }

    @ParameterizedTest(name = "unknown plan type [{0}] falls back to NoInterestPolicy")
    @CsvSource(
        "unknown",
        "Basic",
        "STUDENT",
        "''",
    )
    fun `falls back to NoInterestPolicy for any unrecognized plan type`(planType: String) {
        assertThat(resolver.resolve(planType)).isSameAs(NoInterestPolicy)
    }
}
