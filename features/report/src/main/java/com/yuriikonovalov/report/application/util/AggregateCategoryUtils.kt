package com.yuriikonovalov.report.application.util

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.report.application.entities.AggregateCategory
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency


fun Map<Category, List<Transaction>>.mapToAggregateCategoryList(): List<AggregateCategory> {
    return this.map { entry ->
        AggregateCategory(
            id = entry.key.id,
            name = entry.key.name,
            type = entry.key.type.toAggregateCategoryType(),
            color = entry.key.color,
            iconPath = entry.key.iconPath,
            amounts = entry.value.aggregateAmounts(),
            numberOfTransactions = entry.value.count(),
        )
    }
}


fun List<AggregateCategory>.mapToAggregateCategoryFlatByCurrency(): List<AggregateCategoryFlatByCurrency> {
    return this.flatMap { aggregateCategory ->
        aggregateCategory.amounts.map { aggregateAmount ->
            AggregateCategoryFlatByCurrency(
                id = aggregateCategory.id + System.currentTimeMillis(),
                name = aggregateCategory.name,
                type = aggregateCategory.type,
                currency = aggregateAmount.currency,
                color = aggregateCategory.color,
                iconPath = aggregateCategory.iconPath,
                amount = aggregateAmount.amount,
                numberOfTransactions = aggregateAmount.numberOfTransactions,
            )
        }
    }
}

fun List<Transfer>.mapToIncomeTransaction(): List<Transaction> {
    return this.map {
        it.toIncomeTransaction()
    }
}

fun List<Transfer>.mapToExpenseTransaction(): List<Transaction> {
    return this.map {
        it.toExpenseTransaction()
    }
}

private fun Transfer.toIncomeTransaction(): Transaction {
    return Transaction(
        id = System.currentTimeMillis(),
        account = accountTo,
        category = CategoryMock.transfer(TransactionType.INCOME),
        type = TransactionType.INCOME,
        tags = tags,
        note = note,
        date = date,
        amount = amountTo,
        imagePath = null
    )
}

private fun Transfer.toExpenseTransaction(): Transaction {
    return Transaction(
        id = System.currentTimeMillis(),
        account = accountFrom,
        category = CategoryMock.transfer(TransactionType.EXPENSE),
        type = TransactionType.EXPENSE,
        tags = tags,
        note = note,
        date = date,
        amount = amountFrom,
        imagePath = null
    )
}


private fun List<Transaction>.aggregateAmounts(): List<AggregateCategory.AggregateAmount> {
    return this.groupBy { it.account.currency }
        .map { entry ->
            AggregateCategory.AggregateAmount(
                currency = entry.key,
                numberOfTransactions = entry.value.size,
                amount = entry.value.sumOf { it.amount }
            )
        }
}


private fun CategoryType.toAggregateCategoryType(): AggregateCategory.Type {
    return when (this) {
        CategoryType.INCOME -> AggregateCategory.Type.INCOME
        CategoryType.EXPENSE -> AggregateCategory.Type.EXPENSE
    }
}
