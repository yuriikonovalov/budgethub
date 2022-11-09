package com.yuriikonovalov.report.application.entities

import com.yuriikonovalov.common.application.entities.Currency

/*
* Aggregation:
*   - Category = different transactions;
*       - Different transactions = consist of different currencies (or the same) and a different amount;
*/
data class AggregateCategory(
    val id: Long,
    // name of category as it is;
    val name: String,
    val type: Type,
    val color: Int?,
    val iconPath: String?,
    // if there's transactions with different currencies for this category then this list.size > 1;
    val amounts: List<AggregateAmount>,
    // a total number of transactions for this category;
    val numberOfTransactions: Int
) {
    data class AggregateAmount(
        val currency: Currency,
        val numberOfTransactions: Int,
        // a total amount for a particular currency of the category.
        val amount: Double
    )

    enum class Type {
        INCOME, EXPENSE
    }
}
