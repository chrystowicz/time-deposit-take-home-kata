package org.ikigaidigital

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

/**
 * TimeDepositCalculator's own responsibility is orchestration: for each
 * deposit, resolve a policy, ask it for the raw interest, round that
 * interest, and add it to the balance. The actual interest math and
 * rounding rule are each tested in isolation (InterestPolicyTest,
 * InterestPolicyResolverTest, CurrencyRounderTest), so this suite mocks
 * both collaborators and verifies the orchestration itself.
 */
class TimeDepositCalculatorTest {
    private val policyResolver = mock<InterestPolicyResolver>()
    private val currencyRounder = mock<CurrencyRounder>()
    private val calculator = TimeDepositCalculator(policyResolver, currencyRounder)

    @Test
    fun `resolves the policy for the deposit's plan type, then adds the rounded interest to its balance`() {
        val deposit = TimeDeposit(1, "basic", 1000.00, 31)
        val policy = mock<InterestPolicy>()
        whenever(policyResolver.resolve("basic")).thenReturn(policy)
        whenever(policy.monthlyInterest(deposit)).thenReturn(12.3456)
        whenever(currencyRounder.round(12.3456)).thenReturn(12.35)

        calculator.updateBalance(listOf(deposit))

        verify(policyResolver).resolve("basic")
        verify(currencyRounder).round(12.3456)
        assertThat(deposit.balance).isEqualTo(1012.35)
    }

    @Test
    fun `rounds the raw interest before adding it, never adding the unrounded value`() {
        val deposit = TimeDeposit(1, "basic", 1000.00, 31)
        val policy = mock<InterestPolicy>()
        whenever(policyResolver.resolve(any())).thenReturn(policy)
        whenever(policy.monthlyInterest(any())).thenReturn(0.8333333333333334)
        whenever(currencyRounder.round(0.8333333333333334)).thenReturn(0.83)

        calculator.updateBalance(listOf(deposit))

        verify(currencyRounder).round(0.8333333333333334)
        assertThat(deposit.balance).isEqualTo(1000.83)
    }

    @Test
    fun `each deposit in a list is resolved and updated independently through its own policy`() {
        val basic = TimeDeposit(1, "basic", 1000.00, 31)
        val student = TimeDeposit(2, "student", 2000.00, 31)
        val basicPolicy = mock<InterestPolicy>()
        val studentPolicy = mock<InterestPolicy>()
        whenever(policyResolver.resolve("basic")).thenReturn(basicPolicy)
        whenever(policyResolver.resolve("student")).thenReturn(studentPolicy)
        whenever(basicPolicy.monthlyInterest(basic)).thenReturn(1.0)
        whenever(studentPolicy.monthlyInterest(student)).thenReturn(5.0)
        whenever(currencyRounder.round(1.0)).thenReturn(1.0)
        whenever(currencyRounder.round(5.0)).thenReturn(5.0)

        calculator.updateBalance(listOf(basic, student))

        assertThat(basic.balance).isEqualTo(1001.0)
        assertThat(student.balance).isEqualTo(2005.0)
        verify(basicPolicy).monthlyInterest(basic)
        verify(studentPolicy).monthlyInterest(student)
    }

    @Test
    fun `an empty list of deposits triggers no interaction with either collaborator`() {
        calculator.updateBalance(emptyList())

        verifyNoInteractions(policyResolver)
        verifyNoInteractions(currencyRounder)
    }
}
