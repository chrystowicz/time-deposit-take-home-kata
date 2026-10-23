package org.ikigaidigital.application

import org.ikigaidigital.Withdrawal

/** Port for loading withdrawals. Implemented by an adapter. */
interface WithdrawalRepository {
    fun findAll(): List<Withdrawal>
}
