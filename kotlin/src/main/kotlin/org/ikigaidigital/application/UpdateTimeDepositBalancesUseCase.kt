package org.ikigaidigital.application

import org.ikigaidigital.TimeDepositCalculator

/** Applies this period's interest to every time deposit and persists the result. */
interface UpdateTimeDepositBalancesUseCase {
    fun updateAllBalances()
}

class DefaultUpdateTimeDepositBalancesUseCase(
    private val repository: TimeDepositRepository,
    private val calculator: TimeDepositCalculator,
) : UpdateTimeDepositBalancesUseCase {
    // Known scaffolding-level limitation: findAll -> mutate -> saveAll has no
    // transaction boundary and no optimistic locking, so two concurrent calls
    // would lose one's update, and a crash mid-save leaves a partial write.
    // Acceptable for this task's scope; revisit if concurrent callers become
    // a real scenario (e.g. a transactional repository, or a single-writer
    // scheduler instead of an arbitrary number of HTTP callers).
    override fun updateAllBalances() {
        val deposits = repository.findAll()
        calculator.updateBalance(deposits)
        repository.saveAll(deposits)
    }
}
