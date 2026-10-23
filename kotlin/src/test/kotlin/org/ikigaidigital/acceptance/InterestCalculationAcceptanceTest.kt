package org.ikigaidigital.acceptance

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.TimeDepositCalculator

/**
 * Acceptance tests for README.md's "Interest Calculation" business requirement
 * (Requirement 3), exercised end to end against the real production wiring
 * (TimeDepositCalculator()'s default collaborators, no mocks or stubs).
 *
 * This file is meant to be the single place where every stated interest-rate
 * business rule can be read and seen passing, independent of how the
 * calculation is implemented internally:
 *
 *   - No interest is applied for the first 30 days for any plan.
 *   - Basic Plan: 1% interest.
 *   - Student Plan: 3% interest (no interest after 1 year).
 *   - Premium Plan: 5% interest (interest starts after 45 days).
 *
 * Unit-level detail (exact rounding behavior, orchestration, resolver
 * wiring) is already covered by InterestPolicyTest, CurrencyRounderTest,
 * InterestPolicyResolverTest and TimeDepositCalculatorTest; this suite
 * intentionally stays at business-rule granularity.
 */
class InterestCalculationAcceptanceTest :
    BehaviorSpec({

        // TimeDepositCalculator and its default collaborators are stateless; the
        // only mutable state in play is each test's own TimeDeposit, so sharing
        // one calculator instance across every Given block below is safe.
        val calculator = TimeDepositCalculator()

        Given("no plan earns interest for its first 30 days") {
            When("a brand-new basic plan deposit is 0 days old") {
                val deposit = TimeDeposit(1, "basic", 1000.00, 0)
                calculator.updateBalance(listOf(deposit))
                Then("its balance is unchanged") {
                    deposit.balance shouldBe 1000.00
                }
            }
            When("a basic plan deposit is exactly 30 days old") {
                val deposit = TimeDeposit(1, "basic", 1000.00, 30)
                calculator.updateBalance(listOf(deposit))
                Then("its balance is unchanged") {
                    deposit.balance shouldBe 1000.00
                }
            }
            When("a student plan deposit is exactly 30 days old") {
                val deposit = TimeDeposit(2, "student", 1000.00, 30)
                calculator.updateBalance(listOf(deposit))
                Then("its balance is unchanged") {
                    deposit.balance shouldBe 1000.00
                }
            }
            When("a premium plan deposit is exactly 30 days old") {
                val deposit = TimeDeposit(3, "premium", 1000.00, 30)
                calculator.updateBalance(listOf(deposit))
                Then("its balance is unchanged") {
                    deposit.balance shouldBe 1000.00
                }
            }
        }

        Given("a Basic Plan deposit") {
            When("it is older than 30 days") {
                val deposit = TimeDeposit(1, "basic", 1000.00, 31)
                calculator.updateBalance(listOf(deposit))
                Then("it earns 1% annual interest, applied monthly") {
                    deposit.balance shouldBe 1000.83
                }
            }
            When("it is much older than 30 days") {
                val deposit = TimeDeposit(1, "basic", 1000.00, 365)
                calculator.updateBalance(listOf(deposit))
                Then("it still earns the same 1% monthly interest, with no cutoff") {
                    deposit.balance shouldBe 1000.83
                }
            }
        }

        Given("a Student Plan deposit") {
            When("it is older than 30 days but not yet a year old") {
                val deposit = TimeDeposit(2, "student", 1000.00, 31)
                calculator.updateBalance(listOf(deposit))
                Then("it earns 3% annual interest, applied monthly") {
                    deposit.balance shouldBe 1002.50
                }
            }
            When("it is exactly 1 year (365 days) old") {
                val deposit = TimeDeposit(2, "student", 1000.00, 365)
                calculator.updateBalance(listOf(deposit))
                Then("it still earns interest, right up to the 1-year cutoff") {
                    deposit.balance shouldBe 1002.50
                }
            }
            When("it reaches 1 year and a day (366 days) old") {
                val deposit = TimeDeposit(2, "student", 1000.00, 366)
                calculator.updateBalance(listOf(deposit))
                Then("it no longer earns any interest") {
                    deposit.balance shouldBe 1000.00
                }
            }
        }

        Given("a Premium Plan deposit") {
            When("it is older than 30 days but not yet 45 days old") {
                val deposit = TimeDeposit(3, "premium", 1000.00, 31)
                calculator.updateBalance(listOf(deposit))
                Then("it does not yet earn interest") {
                    deposit.balance shouldBe 1000.00
                }
            }
            When("it is exactly 45 days old") {
                val deposit = TimeDeposit(3, "premium", 1000.00, 45)
                calculator.updateBalance(listOf(deposit))
                Then("it does not yet earn interest") {
                    deposit.balance shouldBe 1000.00
                }
            }
            When("it is older than 45 days") {
                val deposit = TimeDeposit(3, "premium", 1000.00, 46)
                calculator.updateBalance(listOf(deposit))
                Then("it earns 5% annual interest, applied monthly") {
                    deposit.balance shouldBe 1004.17
                }
            }
        }
    })
