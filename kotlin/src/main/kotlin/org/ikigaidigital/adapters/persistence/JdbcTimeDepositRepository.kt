package org.ikigaidigital.adapters.persistence

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.application.TimeDepositRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

/**
 * The only adapter that touches TimeDepositEntity: findAll reads rows as
 * entities then maps each to the domain TimeDeposit before returning, and
 * saveAll maps each domain TimeDeposit to an entity before writing. Nothing
 * outside this class -- including the TimeDepositRepository port it
 * implements -- ever sees TimeDepositEntity.
 */
class JdbcTimeDepositRepository(
    private val jdbcTemplate: JdbcTemplate,
) : TimeDepositRepository {
    override fun findAll(): List<TimeDeposit> =
        jdbcTemplate
            .query("SELECT id, plan_type, days, balance FROM time_deposits", TimeDepositEntityRowMapper)
            .map { it.toDomain() }

    override fun saveAll(deposits: List<TimeDeposit>) {
        // Row-by-row, no batching, and an update matching zero rows (e.g. the
        // row was deleted between findAll and saveAll) is silently ignored --
        // acceptable for this task's scaffolding-level scope.
        for (deposit in deposits) {
            val entity = deposit.toEntity()
            jdbcTemplate.update(
                "UPDATE time_deposits SET plan_type = ?, days = ?, balance = ? WHERE id = ?",
                entity.planType,
                entity.days,
                entity.balance,
                entity.id,
            )
        }
    }

    private object TimeDepositEntityRowMapper : RowMapper<TimeDepositEntity> {
        override fun mapRow(
            rs: java.sql.ResultSet,
            rowNum: Int,
        ): TimeDepositEntity =
            TimeDepositEntity(
                id = rs.getInt("id"),
                planType = rs.getString("plan_type"),
                days = rs.getInt("days"),
                balance = rs.getBigDecimal("balance"),
            )
    }
}
