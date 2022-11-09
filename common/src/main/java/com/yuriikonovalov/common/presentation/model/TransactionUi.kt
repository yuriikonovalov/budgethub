package com.yuriikonovalov.common.presentation.model

import android.content.Context
import android.graphics.Color
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toDdMmmmYyyy
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import com.yuriikonovalov.common.presentation.model.CategoryUi.Companion.colorBackgroundUi
import com.yuriikonovalov.common.presentation.model.CategoryUi.Companion.colorIconUi


data class TransactionUi(
    val categoryIcon: String?,
    val categoryName: String,
    val categoryBackgroundColor: Int,
    val categoryIconColor: Int,
    val currencyCode: String,
    val date: String,
    val amount: String,
) {
    companion object {
        fun from(context: Context, domain: Transaction): TransactionUi {
            return TransactionUi(
                categoryIcon = domain.category?.iconPath ?: "",
                categoryName = mapCategoryName(context, domain.category),
                categoryIconColor = domain.categoryIconColorUi(),
                categoryBackgroundColor = domain.categoryBackgroundColorUi(),
                currencyCode = domain.account.currency.code,
                date = domain.date.toDdMmmmYyyy(),
                amount = domain.amountUi()
            )
        }

        private fun Transaction.categoryIconColorUi(): Int {
            return this.category?.colorIconUi() ?: Color.LTGRAY
        }

        private fun Transaction.categoryBackgroundColorUi(): Int {
            return this.category?.colorBackgroundUi()
                ?: Color.LTGRAY
        }

        private fun mapCategoryName(context: Context, category: Category?): String {
            return if (category != null) {
                NameMapperUi.mapName(context, category.name)
            } else {
                context.getString(R.string.default_uncategorized_category)
            }
        }

        private fun Transaction.amountUi(): String {
            val amountString = MoneyFormat.getStringValue(amount)
            return when (type) {
                TransactionType.INCOME -> "+ $amountString"
                TransactionType.EXPENSE -> "- $amountString"
            }
        }
    }
}
