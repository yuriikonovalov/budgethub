package com.yuriikonovalov.report.application.entities

data class CategoryWithNumberOfTransactions(
    val id: Long,
    val name: String,
    val type: Type,
    val color: Int?,
    val iconPath: String?,
    val numberOfTransactions: Int,
    val percentFractionOfAllTransaction: Double
) {
    enum class Type {
        INCOME, EXPENSE, TRANSFER
    }
}
