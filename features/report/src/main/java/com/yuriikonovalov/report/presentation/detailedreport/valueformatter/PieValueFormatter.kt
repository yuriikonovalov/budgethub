package com.yuriikonovalov.report.presentation.detailedreport.valueformatter

import com.github.mikephil.charting.formatter.ValueFormatter

class PieValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "$value %"
    }
}