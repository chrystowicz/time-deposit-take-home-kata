package org.ikigaidigital.core.deposit

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.TimeDeposit
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class GetAllTimeDepositsUseCaseTest {
    private val timeDepositRepository = mock<TimeDepositRepository>()
    private val withdrawalRepository = mock<WithdrawalRepository>()
    private val useCase = GetAllTimeDepositsUseCase(timeDepositRepository, withdrawalRepository)

    @Test
    fun `groups each time deposit with only the withdrawals that reference it`() {
        val deposit1 = TimeDeposit(1, "basic", 1000.00, 31)
        val deposit2 = TimeDeposit(2, "premium", 5000.00, 46)
        val withdrawal1 = Withdrawal(1, timeDepositId = 1, amount = 100.0, date = LocalDate.of(2024, 1, 15))
        val withdrawal2 = Withdrawal(2, timeDepositId = 2, amount = 50.0, date = LocalDate.of(2024, 2, 1))
        whenever(timeDepositRepository.findAll()).thenReturn(listOf(deposit1, deposit2))
        whenever(withdrawalRepository.findAll()).thenReturn(listOf(withdrawal1, withdrawal2))

        val result = useCase.getAll()

        assertThat(result).containsExactlyInAnyOrder(
            TimeDepositWithWithdrawals(deposit1, listOf(withdrawal1)),
            TimeDepositWithWithdrawals(deposit2, listOf(withdrawal2)),
        )
    }

    @Test
    fun `a time deposit with multiple withdrawals gets every one of them, not just one`() {
        val deposit = TimeDeposit(1, "basic", 1000.00, 31)
        val withdrawal1 = Withdrawal(1, timeDepositId = 1, amount = 100.0, date = LocalDate.of(2024, 1, 15))
        val withdrawal2 = Withdrawal(2, timeDepositId = 1, amount = 50.0, date = LocalDate.of(2024, 2, 1))
        whenever(timeDepositRepository.findAll()).thenReturn(listOf(deposit))
        whenever(withdrawalRepository.findAll()).thenReturn(listOf(withdrawal1, withdrawal2))

        val result = useCase.getAll()

        assertThat(result).containsExactly(
            TimeDepositWithWithdrawals(deposit, listOf(withdrawal1, withdrawal2)),
        )
    }

    @Test
    fun `a time deposit with no withdrawals gets an empty list, not a missing entry`() {
        val deposit = TimeDeposit(1, "basic", 1000.00, 31)
        whenever(timeDepositRepository.findAll()).thenReturn(listOf(deposit))
        whenever(withdrawalRepository.findAll()).thenReturn(emptyList())

        val result = useCase.getAll()

        assertThat(result).containsExactly(TimeDepositWithWithdrawals(deposit, emptyList()))
    }
}
