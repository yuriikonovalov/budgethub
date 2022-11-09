package com.yuriikonovalov.report.presentation.detailedreport.valueformatter

import com.github.mikephil.charting.formatter.ValueFormatter
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat

class BarValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return MoneyFormat.getStringValue(value.toDouble())
    }
}