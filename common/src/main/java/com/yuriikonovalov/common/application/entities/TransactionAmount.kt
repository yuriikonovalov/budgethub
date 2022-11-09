package com.yuriikonovalov.common.application.entities

data class TransactionAmount(
    val amount: Double,
    val currency: Currency,
    val type: Type
) {
    enum class Type {
        INCOME, EXPENSE
    }
}