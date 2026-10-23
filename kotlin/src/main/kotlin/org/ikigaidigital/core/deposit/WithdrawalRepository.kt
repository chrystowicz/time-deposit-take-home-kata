package org.ikigaidigital.core.deposit

interface WithdrawalRepository {
    fun findAll(): List<Withdrawal>
}
