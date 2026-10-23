package org.ikigaidigital.adapters.config

import org.ikigaidigital.CurrencyRounder
import org.ikigaidigital.HalfUpCurrencyRounder
import org.ikigaidigital.InterestPolicyResolver
import org.ikigaidigital.PlanTypeInterestPolicyResolver
import org.ikigaidigital.TimeDepositCalculator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Decides which concrete implementation backs each domain interface. Nothing
 * outside this class (or a future configuration like it) should ever name a
 * concrete InterestPolicy/InterestPolicyResolver/CurrencyRounder type directly
 * -- every other Spring-managed component should depend only on the
 * interfaces, exactly as TimeDepositCalculator itself already does.
 *
 * Swapping, say, HalfUpCurrencyRounder for a different rounding rule, or
 * adding a new plan type's InterestPolicy, means changing only this file.
 */
@Configuration
class DomainBeansConfiguration {
    @Bean
    fun interestPolicyResolver(): InterestPolicyResolver = PlanTypeInterestPolicyResolver()

    @Bean
    fun currencyRounder(): CurrencyRounder = HalfUpCurrencyRounder()

    @Bean
    fun timeDepositCalculator(
        interestPolicyResolver: InterestPolicyResolver,
        currencyRounder: CurrencyRounder,
    ): TimeDepositCalculator = TimeDepositCalculator(interestPolicyResolver, currencyRounder)
}
