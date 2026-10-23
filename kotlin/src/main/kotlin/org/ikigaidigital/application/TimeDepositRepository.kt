package org.ikigaidigital.application

import org.ikigaidigital.TimeDeposit

/** Port for loading and persisting time deposits. Implemented by an adapter. */
interface TimeDepositRepository {
    fun findAll(): List<TimeDeposit>

    fun saveAll(deposits: List<TimeDeposit>)
}
