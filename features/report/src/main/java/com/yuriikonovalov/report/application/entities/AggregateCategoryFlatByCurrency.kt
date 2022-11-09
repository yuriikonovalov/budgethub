package com.yuriikonovalov.report.application.entities

import com.yuriikonovalov.common.application.entities.Currency

data class AggregateCategoryFlatByCurrency(
    val id: Long,
    val name: String,
    val type: AggregateCategory.Type,
    val color: Int?,
    val currency: Currency,
    val iconPath: String?,
    val numberOfTransactions: Int,
    val amount: Double,
)
