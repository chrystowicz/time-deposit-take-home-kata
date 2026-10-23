package org.ikigaidigital.adapters

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["org.ikigaidigital"])
class TimeDepositApplication

fun main(args: Array<String>) {
    runApplication<TimeDepositApplication>(*args)
}
