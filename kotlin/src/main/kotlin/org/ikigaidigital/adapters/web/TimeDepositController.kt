package org.ikigaidigital.adapters.web

import org.ikigaidigital.adapters.web.generated.TimeDepositsApi
import org.ikigaidigital.application.UpdateTimeDepositBalancesUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Implements the contract generated from src/main/resources/openapi/time-deposit-api.yaml
 * (see build.gradle.kts's openApiGenerate task). TimeDepositsApi carries the
 * @RequestMapping path for each operation, so this class adds no routing
 * paths of its own -- changing an operation's signature in the YAML forces
 * a compile error here. Note that is NOT the same as forcing every
 * operation to be implemented: kotlin-spring generates default (non-abstract)
 * method bodies that return 501, so a *newly added* operation left
 * unimplemented would compile fine and fail only at runtime --
 * TimeDepositControllerContractTest guards against that gap. @RestController
 * on the interface alone is not enough for Spring's handler-mapping/
 * component-scan detection to find this bean as a controller, so it's
 * re-declared here on the implementing class.
 */
@RestController
class TimeDepositController(
    private val updateTimeDepositBalancesUseCase: UpdateTimeDepositBalancesUseCase,
) : TimeDepositsApi {
    override fun updateBalances(): ResponseEntity<Unit> {
        updateTimeDepositBalancesUseCase.updateAllBalances()
        return ResponseEntity.noContent().build()
    }
}
