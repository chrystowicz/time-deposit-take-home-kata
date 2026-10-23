package org.ikigaidigital.adapters.persistence

import org.ikigaidigital.Withdrawal
import org.ikigaidigital.application.WithdrawalRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

/**
 * The only adapter that touches WithdrawalEntity: findAll reads rows as
 * entities then maps each to the domain Withdrawal before returning.
 */
class JdbcWithdrawalRepository(
    private val jdbcTemplate: JdbcTemplate,
) : WithdrawalRepository {
    override fun findAll(): List<Withdrawal> =
        jdbcTemplate
            .query("SELECT id, time_deposit_id, amount, date FROM withdrawals", WithdrawalEntityRowMapper)
            .map { it.toDomain() }

    private object WithdrawalEntityRowMapper : RowMapper<WithdrawalEntity> {
        override fun mapRow(
            rs: java.sql.ResultSet,
            rowNum: Int,
        ): WithdrawalEntity =
            WithdrawalEntity(
                id = rs.getInt("id"),
                timeDepositId = rs.getInt("time_deposit_id"),
                amount = rs.getBigDecimal("amount"),
                date = rs.getDate("date").toLocalDate(),
            )
    }
}
