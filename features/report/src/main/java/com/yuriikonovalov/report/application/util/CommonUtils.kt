package com.yuriikonovalov.report.application.util

import com.yuriikonovalov.common.application.entities.transaction.Transaction

fun List<Transaction>.swapNullCategoryToUncategorized(): List<Transaction> {
    return this.map { transaction ->
        if (transaction.category == null) {
            transaction.categoryToUncategorized()
        } else {
            transaction
        }
    }
}

private fun Transaction.categoryToUncategorized(): Transaction {
    return this.copy(
        category = CategoryMock.uncategorized(type)
    )
}