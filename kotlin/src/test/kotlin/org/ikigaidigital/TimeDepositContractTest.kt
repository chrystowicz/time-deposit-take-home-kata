package org.ikigaidigital

import org.junit.jupiter.api.Test

class TimeDepositContractTest {

    @Test
    fun `public API of TimeDeposit and updateBalance stays unchanged`() {
        // Compiles only while the constructor and updateBalance keep their current signatures.
        val deposit = TimeDeposit(1, "basic", 1234567.00, 45)
        val calculator = TimeDepositCalculator()
        calculator.updateBalance(listOf(deposit))
    }

}