package com.yuriikonovalov.report.presentation.model

import android.graphics.Color
import com.yuriikonovalov.common.framework.common.extentions.roundDecimals
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency

data class PieChartAggregateCurrencyUi(
    val color: Int,
    val percent: Double,
) {
    companion object {
        fun from(
            domain: AggregateCategoryFlatByCurrency,
            totalAmount: Double
        ): PieChartAggregateCurrencyUi {
            return PieChartAggregateCurrencyUi(
                color = domain.colorUi(),
                percent = domain.amountPercent(totalAmount)
            )
        }

        fun fromList(domain: List<AggregateCategoryFlatByCurrency>): List<PieChartAggregateCurrencyUi> {
            val totalAmount = domain.sumOf { it.amount }
            return domain.map { associatedTransaction ->
                from(associatedTransaction, totalAmount)
            }
        }

        private fun AggregateCategoryFlatByCurrency.amountPercent(totalAmount: Double): Double {
            return (amount * 100 / totalAmount).roundDecimals(2)
        }


        private fun AggregateCategoryFlatByCurrency.colorUi(): Int {
            return color ?: Color.LTGRAY
        }
    }
}
