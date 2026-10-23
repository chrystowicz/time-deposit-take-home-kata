package org.ikigaidigital.adapters

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * The only place in this codebase that knows about Spring. Domain code under
 * org.ikigaidigital (TimeDeposit, TimeDepositCalculator, InterestPolicy, ...)
 * has zero framework annotations; everything Spring-specific lives under
 * org.ikigaidigital.adapters, explicitly scanning the domain package so that
 * configuration classes here can wire plain Kotlin domain objects as beans
 * (see [org.ikigaidigital.adapters.config.DomainBeansConfiguration]).
 */
@SpringBootApplication(scanBasePackages = ["org.ikigaidigital"])
class TimeDepositApplication

fun main(args: Array<String>) {
    runApplication<TimeDepositApplication>(*args)
}
