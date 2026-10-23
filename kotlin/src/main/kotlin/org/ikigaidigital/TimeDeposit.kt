package org.ikigaidigital // left in this package due to backward compatibility

data class TimeDeposit(
    val id: Int,
    val planType: String,
    // mutability intentionally left, but that's a code smell; also use BigDecimal along with currency whenever possible
    var balance: Double,
    val days: Int,
)
