package org.ikigaidigital.adapters.persistence

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.application.TimeDepositRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcTimeDepositRepository(
    private val jdbcTemplate: JdbcTemplate,
) : TimeDepositRepository {
    override fun findAll(): List<TimeDeposit> =
        jdbcTemplate.query("SELECT id, plan_type, days, balance FROM time_deposits", TimeDepositRowMapper)

    override fun saveAll(deposits: List<TimeDeposit>) {
        // Row-by-row, no batching, and an update matching zero rows (e.g. the
        // row was deleted between findAll and saveAll) is silently ignored --
        // acceptable for this task's scaffolding-level scope.
        for (deposit in deposits) {
            jdbcTemplate.update(
                "UPDATE time_deposits SET plan_type = ?, days = ?, balance = ? WHERE id = ?",
                deposit.planType,
                deposit.days,
                deposit.balance,
                deposit.id,
            )
        }
    }

    private object TimeDepositRowMapper : RowMapper<TimeDeposit> {
        override fun mapRow(
            rs: java.sql.ResultSet,
            rowNum: Int,
        ): TimeDeposit =
            TimeDeposit(
                id = rs.getInt("id"),
                planType = rs.getString("plan_type"),
                balance = rs.getBigDecimal("balance").toDouble(),
                days = rs.getInt("days"),
            )
    }
}
