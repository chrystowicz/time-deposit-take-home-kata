package org.ikigaidigital.application

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.TimeDepositCalculator
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

/**
 * UpdateTimeDepositBalancesUseCase's own job is orchestration: load every
 * deposit, hand the whole list to the calculator (which mutates balances in
 * place), then persist that same list back. The calculator's actual interest
 * math is tested elsewhere (InterestPolicyTest, InterestCalculationAcceptanceTest),
 * so this suite mocks both collaborators and verifies the orchestration itself.
 */
class UpdateTimeDepositBalancesUseCaseTest {
    private val repository = mock<TimeDepositRepository>()
    private val calculator = mock<TimeDepositCalculator>()
    private val useCase = DefaultUpdateTimeDepositBalancesUseCase(repository, calculator)

    @Test
    fun `loads every deposit, updates their balances, then saves the same list back`() {
        val deposits = listOf(TimeDeposit(1, "basic", 1000.00, 31))
        whenever(repository.findAll()).thenReturn(deposits)

        useCase.updateAllBalances()

        verify(repository).findAll()
        verify(calculator).updateBalance(deposits)
        verify(repository).saveAll(deposits)
    }

    @Test
    fun `an empty repository still goes through the full load-update-save sequence`() {
        whenever(repository.findAll()).thenReturn(emptyList())

        useCase.updateAllBalances()

        verify(repository).findAll()
        verify(calculator).updateBalance(emptyList())
        verify(repository).saveAll(emptyList())
        verifyNoMoreInteractions(repository, calculator)
    }
}
