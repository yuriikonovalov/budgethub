package com.yuriikonovalov.report.application.util

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.util.TRANSFER_NAME
import com.yuriikonovalov.report.application.entities.CategoryWithNumberOfTransactions

fun List<Transfer>.mapToCategoryWithNumberOfTransactions(totalNumberOfTransactions: Int): CategoryWithNumberOfTransactions? {
    return if (this.isNotEmpty()) {
        val percent = this.size * 100.0 / totalNumberOfTransactions
        CategoryWithNumberOfTransactions(
            // Use -1. All transfers are one category if look at it as a CategoryWithNumberOfTransactions.
            id = -1,
            name = TRANSFER_NAME,
            type = CategoryWithNumberOfTransactions.Type.TRANSFER,
            color = null,
            iconPath = null,
            numberOfTransactions = this.size,
            percentFractionOfAllTransaction = percent
        )
    } else {
        null
    }
}

fun Map<Category, List<Transaction>>.mapToCategoryWithNumberOfTransactionsList(
    totalNumberOfTransactions: Int
): List<CategoryWithNumberOfTransactions> {
    return this.map { entry ->
        val percent = entry.value.size * 100.0 / totalNumberOfTransactions
        CategoryWithNumberOfTransactions(
            id = entry.key.id,
            name = entry.key.name,
            type = entry.key.type.toCategoryWithNumberOfTransactionsType(),
            color = entry.key.color,
            iconPath = entry.key.iconPath,
            numberOfTransactions = entry.value.count(),
            percentFractionOfAllTransaction = percent
        )
    }
}


private fun CategoryType.toCategoryWithNumberOfTransactionsType(): CategoryWithNumberOfTransactions.Type {
    return when (this) {
        CategoryType.INCOME -> CategoryWithNumberOfTransactions.Type.INCOME
        CategoryType.EXPENSE -> CategoryWithNumberOfTransactions.Type.EXPENSE
    }
}

