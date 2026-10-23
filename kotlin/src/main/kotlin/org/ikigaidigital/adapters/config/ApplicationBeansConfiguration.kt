package org.ikigaidigital.adapters.config

import org.ikigaidigital.TimeDepositCalculator
import org.ikigaidigital.adapters.persistence.JdbcTimeDepositRepository
import org.ikigaidigital.adapters.persistence.JdbcWithdrawalRepository
import org.ikigaidigital.application.DefaultGetAllTimeDepositsUseCase
import org.ikigaidigital.application.DefaultUpdateTimeDepositBalancesUseCase
import org.ikigaidigital.application.GetAllTimeDepositsUseCase
import org.ikigaidigital.application.TimeDepositRepository
import org.ikigaidigital.application.UpdateTimeDepositBalancesUseCase
import org.ikigaidigital.application.WithdrawalRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

/**
 * Decides which concrete implementation backs each application-layer
 * interface, same pattern as DomainBeansConfiguration: everything outside
 * this class depends only on TimeDepositRepository/UpdateTimeDepositBalancesUseCase,
 * never on JdbcTimeDepositRepository/DefaultUpdateTimeDepositBalancesUseCase directly.
 */
@Configuration
class ApplicationBeansConfiguration {
    @Bean
    fun timeDepositRepository(jdbcTemplate: JdbcTemplate): TimeDepositRepository = JdbcTimeDepositRepository(jdbcTemplate)

    @Bean
    fun withdrawalRepository(jdbcTemplate: JdbcTemplate): WithdrawalRepository = JdbcWithdrawalRepository(jdbcTemplate)

    @Bean
    fun updateTimeDepositBalancesUseCase(
        timeDepositRepository: TimeDepositRepository,
        timeDepositCalculator: TimeDepositCalculator,
    ): UpdateTimeDepositBalancesUseCase = DefaultUpdateTimeDepositBalancesUseCase(timeDepositRepository, timeDepositCalculator)

    @Bean
    fun getAllTimeDepositsUseCase(
        timeDepositRepository: TimeDepositRepository,
        withdrawalRepository: WithdrawalRepository,
    ): GetAllTimeDepositsUseCase = DefaultGetAllTimeDepositsUseCase(timeDepositRepository, withdrawalRepository)
}
