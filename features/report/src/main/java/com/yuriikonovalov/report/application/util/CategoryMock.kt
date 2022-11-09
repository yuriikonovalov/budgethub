package com.yuriikonovalov.report.application.util

import android.graphics.Color
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.util.TRANSFER_ICON
import com.yuriikonovalov.common.application.util.TRANSFER_NAME
import com.yuriikonovalov.common.application.util.UNCATEGORIZED_ICON
import com.yuriikonovalov.common.application.util.UNCATEGORIZED_NAME

object CategoryMock {
    // Categories saved in DB can have an ID greater than 0, so -1 and -2 are free to use.
    private const val MOCK_UNCATEGORIZED_ID = -1L
    private const val MOCK_TRANSFER_ID = -2L
    fun uncategorized(type: TransactionType) = Category(
        id = MOCK_UNCATEGORIZED_ID,
        type = type.toCategoryType(),
        name = UNCATEGORIZED_NAME,
        isCustom = false,
        iconPath = UNCATEGORIZED_ICON,
        color = Color.BLACK
    )


    fun transfer(type: TransactionType) = Category(
        id = MOCK_TRANSFER_ID,
        type = type.toCategoryType(),
        name = TRANSFER_NAME,
        isCustom = false,
        iconPath = TRANSFER_ICON,
        color = Color.BLACK
    )

    private fun TransactionType.toCategoryType(): CategoryType {
        return when (this) {
            TransactionType.EXPENSE -> CategoryType.EXPENSE
            TransactionType.INCOME -> CategoryType.INCOME
        }
    }
}