package org.ikigaidigital.adapters.persistence

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.TimeDeposit
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * TimeDepositEntity is the persistence-shaped representation (balance as
 * BigDecimal, matching the DECIMAL(19,2) column) -- intentionally distinct
 * from the domain TimeDeposit (balance as Double, frozen by CLAUDE.md's
 * hard constraint). This pins down the translation at that boundary.
 */
class TimeDepositMapperTest {
    @Test
    fun `maps an entity to its domain representation`() {
        val entity = TimeDepositEntity(id = 1, planType = "basic", days = 31, balance = BigDecimal("1000.83"))

        val domain = entity.toDomain()

        assertThat(domain).isEqualTo(TimeDeposit(id = 1, planType = "basic", balance = 1000.83, days = 31))
    }

    @Test
    fun `maps a domain time deposit to its persistence representation`() {
        val domain = TimeDeposit(id = 1, planType = "basic", balance = 1000.83, days = 31)

        val entity = domain.toEntity()

        assertThat(entity).isEqualTo(TimeDepositEntity(id = 1, planType = "basic", days = 31, balance = BigDecimal("1000.83")))
    }

    @Test
    fun `round-tripping through the entity preserves every field`() {
        val original = TimeDeposit(id = 42, planType = "premium", balance = 5000.17, days = 46)

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
    }

    @Test
    fun `a whole-number balance maps to a scale-2 entity, matching the DECIMAL(19,2) column`() {
        // Without normalizing scale, 1000.0 would map to BigDecimal("1000")
        // (scale 0) -- numerically correct but NOT equal (BigDecimal.equals
        // is scale-sensitive) to BigDecimal("1000.00") (scale 2), which is
        // what a row read back from the database would actually produce.
        val domain = TimeDeposit(id = 1, planType = "basic", balance = 1000.0, days = 31)

        val entity = domain.toEntity()

        assertThat(entity).isEqualTo(TimeDepositEntity(id = 1, planType = "basic", days = 31, balance = BigDecimal("1000.00")))
    }
}
