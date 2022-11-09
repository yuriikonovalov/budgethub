package com.yuriikonovalov.report.presentation.model

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.yuriikonovalov.common.*
import com.yuriikonovalov.common.framework.common.extentions.roundDecimals
import com.yuriikonovalov.common.presentation.model.NameMapperUi
import com.yuriikonovalov.report.application.entities.CategoryWithNumberOfTransactions

data class CategoryWithNumberOfTransactionsUi(
    val name: String,
    val colorIcon: Int,
    val colorBackground: Int,
    val iconPath: String?,
    val numberOfTransactions: String,
    val percentOfAllTransaction: String
) {
    companion object {
        fun from(
            context: Context,
            domain: CategoryWithNumberOfTransactions
        ): CategoryWithNumberOfTransactionsUi {
            return CategoryWithNumberOfTransactionsUi(
                name = domain.nameUi(context),
                colorIcon = domain.colorIconUi(),
                colorBackground = domain.colorBackgroundUi(),
                iconPath = domain.iconPathUi(),
                numberOfTransactions = domain.numberOfTransactionsUi(context),
                percentOfAllTransaction = domain.percentOfAllTransactionUi()
            )
        }

        private fun CategoryWithNumberOfTransactions.iconPathUi(): String? {
            return when (this.type) {
                CategoryWithNumberOfTransactions.Type.TRANSFER -> "file:///android_asset/icons/default/transfer.svg"
                else -> this.iconPath
            }
        }

        private fun CategoryWithNumberOfTransactions.percentOfAllTransactionUi(): String {
            return "${percentFractionOfAllTransaction.roundDecimals(2)}%"
        }

        private fun CategoryWithNumberOfTransactions.numberOfTransactionsUi(context: Context): String {
            return context.resources.getQuantityString(
                R.plurals.transactions,
                numberOfTransactions,
                numberOfTransactions
            )
        }

        private fun CategoryWithNumberOfTransactions.nameUi(context: Context): String {
            return NameMapperUi.mapName(context, name)
        }

        private fun CategoryWithNumberOfTransactions.colorBackgroundUi(): Int {
            return this.color?.let { ColorUtils.setAlphaComponent(it, 50) }
                ?: ColorUtils.setAlphaComponent(Color.DKGRAY, 50)
        }

        private fun CategoryWithNumberOfTransactions.colorIconUi(): Int {
            return color ?: Color.DKGRAY
        }
    }
}
