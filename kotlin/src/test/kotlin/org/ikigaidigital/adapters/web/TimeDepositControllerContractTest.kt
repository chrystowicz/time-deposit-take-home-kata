package org.ikigaidigital.adapters.web

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.adapters.web.generated.TimeDepositsApi
import org.junit.jupiter.api.Test

/**
 * kotlin-spring's generated Api interfaces give every operation a *default*
 * body (HTTP 501) rather than making it abstract, so the Kotlin compiler
 * does NOT force an implementing class to override a newly-added operation
 * -- forgetting to do so would silently fall back to "Not Implemented" at
 * runtime, not a compile error. Changing an *existing* operation's
 * signature in the YAML does still break the build (TimeDepositController
 * would no longer compile as an override), but a missing new operation
 * would not. This test closes that specific gap: it fails loudly if
 * TimeDepositController ever stops overriding any operation the generated
 * contract declares.
 */
class TimeDepositControllerContractTest {
    @Test
    fun `TimeDepositController overrides every operation declared by the generated contract`() {
        val generatedOperations = TimeDepositsApi::class.java.declaredMethods.filter { !it.isSynthetic }

        assertThat(generatedOperations).isNotEmpty()
        generatedOperations.forEach { operation ->
            val controllerMethod =
                TimeDepositController::class.java.getMethod(operation.name, *operation.parameterTypes)
            assertThat(controllerMethod.declaringClass)
                .`as`(
                    "TimeDepositController must override %s, not fall back to the generated default (501 Not Implemented)",
                    operation.name,
                ).isEqualTo(TimeDepositController::class.java)
        }
    }
}
