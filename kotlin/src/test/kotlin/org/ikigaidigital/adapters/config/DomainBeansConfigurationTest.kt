package org.ikigaidigital.adapters.config

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.CurrencyRounder
import org.ikigaidigital.InterestPolicyResolver
import org.ikigaidigital.PlanTypeInterestPolicyResolver
import org.ikigaidigital.TimeDepositCalculator
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.runner.ApplicationContextRunner

/**
 * Verifies DomainBeansConfiguration's DI wiring in isolation, with no
 * database/Docker/full Spring Boot context involved: only the configuration
 * class itself is loaded (via Spring's lightweight ApplicationContextRunner),
 * which is all that's needed to prove "configuration classes decide the
 * implementation, everything else depends only on the interface."
 */
class DomainBeansConfigurationTest {
    private val contextRunner = ApplicationContextRunner().withUserConfiguration(DomainBeansConfiguration::class.java)

    @Test
    fun `wires InterestPolicyResolver as its interface type, backed by the real resolver`() {
        contextRunner.run { context ->
            assertThat(context).hasSingleBean(InterestPolicyResolver::class.java)
            assertThat(context.getBean(InterestPolicyResolver::class.java))
                .isInstanceOf(PlanTypeInterestPolicyResolver::class.java)
        }
    }

    @Test
    fun `wires CurrencyRounder as its interface type`() {
        contextRunner.run { context ->
            assertThat(context).hasSingleBean(CurrencyRounder::class.java)
        }
    }

    @Test
    fun `wires TimeDepositCalculator from the InterestPolicyResolver and CurrencyRounder beans`() {
        contextRunner.run { context ->
            assertThat(context).hasSingleBean(TimeDepositCalculator::class.java)
        }
    }
}
