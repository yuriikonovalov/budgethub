package com.yuriikonovalov.common.application.entities.transaction

import com.yuriikonovalov.common.application.entities.category.CategoryType

enum class TransactionType {
    INCOME, EXPENSE
}

/**
 * A bridge between [CategoryType] and [TransactionType] for comparing values.
 */
fun TransactionType.equalsTo(categoryType: CategoryType): Boolean {
    return when {
        this == TransactionType.INCOME && categoryType == CategoryType.INCOME -> true
        this == TransactionType.EXPENSE && categoryType == CategoryType.EXPENSE -> true
        else -> false
    }
}