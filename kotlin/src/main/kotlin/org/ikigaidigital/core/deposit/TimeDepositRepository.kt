package org.ikigaidigital.core.deposit

import org.ikigaidigital.TimeDeposit

interface TimeDepositRepository {
    fun findAll(): List<TimeDeposit>

    fun saveAll(deposits: List<TimeDeposit>)
}
