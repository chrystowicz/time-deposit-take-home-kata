package org.ikigaidigital.adapters.config

import org.ikigaidigital.adapters.persistence.JdbcTimeDepositRepository
import org.ikigaidigital.adapters.persistence.JdbcWithdrawalRepository
import org.ikigaidigital.core.deposit.GetAllTimeDepositsUseCase
import org.ikigaidigital.core.deposit.TimeDepositCalculator
import org.ikigaidigital.core.deposit.TimeDepositRepository
import org.ikigaidigital.core.deposit.UpdateTimeDepositBalancesUseCase
import org.ikigaidigital.core.deposit.WithdrawalRepository
import org.ikigaidigital.core.interest.InterestPolicyResolver
import org.ikigaidigital.core.utils.CurrencyRounder
import org.ikigaidigital.core.utils.HalfUpCurrencyRounder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class ApplicationBeansConfiguration {
    @Bean
    fun timeDepositRepository(jdbcTemplate: JdbcTemplate): TimeDepositRepository = JdbcTimeDepositRepository(jdbcTemplate)

    @Bean
    fun withdrawalRepository(jdbcTemplate: JdbcTemplate): WithdrawalRepository = JdbcWithdrawalRepository(jdbcTemplate)

    @Bean
    fun interestPolicyResolver(): InterestPolicyResolver = InterestPolicyResolver()

    @Bean
    fun currencyRounder(): CurrencyRounder = HalfUpCurrencyRounder()

    @Bean
    fun timeDepositCalculator(
        interestPolicyResolver: InterestPolicyResolver,
        currencyRounder: CurrencyRounder,
    ): TimeDepositCalculator =
        TimeDepositCalculator(
            interestPolicyResolver,
            currencyRounder,
        )

    @Bean
    fun updateTimeDepositBalancesUseCase(
        timeDepositRepository: TimeDepositRepository,
        timeDepositCalculator: TimeDepositCalculator,
    ): UpdateTimeDepositBalancesUseCase = UpdateTimeDepositBalancesUseCase(timeDepositRepository, timeDepositCalculator)

    @Bean
    fun getAllTimeDepositsUseCase(
        timeDepositRepository: TimeDepositRepository,
        withdrawalRepository: WithdrawalRepository,
    ): GetAllTimeDepositsUseCase = GetAllTimeDepositsUseCase(timeDepositRepository, withdrawalRepository)
}
