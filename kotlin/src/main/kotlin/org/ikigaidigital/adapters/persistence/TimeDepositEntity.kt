package org.ikigaidigital.adapters.persistence

import java.math.BigDecimal

/**
 * The persistence-shaped representation of a time deposit row: field types
 * mirror the time_deposits table (see schema.sql) exactly -- balance as
 * BigDecimal, matching the DECIMAL(19,2) column -- rather than the domain
 * TimeDeposit's shape (balance as Double, frozen by CLAUDE.md's hard
 * constraint on TimeDeposit.kt). Entities and the domain model are allowed
 * to diverge and evolve independently; TimeDepositMapper is the only place
 * that translates between them, and JdbcTimeDepositRepository is the only
 * adapter that should ever construct or read a TimeDepositEntity -- nothing
 * past the TimeDepositRepository port boundary sees this type.
 */
data class TimeDepositEntity(
    val id: Int,
    val planType: String,
    val days: Int,
    val balance: BigDecimal,
)
