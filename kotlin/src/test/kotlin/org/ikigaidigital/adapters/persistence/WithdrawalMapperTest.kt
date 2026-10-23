package org.ikigaidigital.adapters.persistence

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.Withdrawal
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

/**
 * WithdrawalEntity is the persistence-shaped representation (amount as
 * BigDecimal, matching the DECIMAL(19,2) column) -- this pins down the
 * translation at that boundary, same as TimeDepositMapperTest does for
 * TimeDepositEntity.
 */
class WithdrawalMapperTest {
    @Test
    fun `maps an entity to its domain representation`() {
        val entity =
            WithdrawalEntity(id = 1, timeDepositId = 10, amount = BigDecimal("100.50"), date = LocalDate.of(2024, 1, 15))

        val domain = entity.toDomain()

        assertThat(domain).isEqualTo(
            Withdrawal(id = 1, timeDepositId = 10, amount = 100.50, date = LocalDate.of(2024, 1, 15)),
        )
    }
}
