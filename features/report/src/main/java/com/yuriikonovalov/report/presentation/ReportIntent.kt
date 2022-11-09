package com.yuriikonovalov.report.presentation

import com.yuriikonovalov.common.application.entities.PeriodFilter

sealed class ReportIntent {
    data class ChangePeriod(val periodFilter: PeriodFilter) : ReportIntent()
    object ClickReportButton : ReportIntent()
    object ClickSeeAllButton : ReportIntent()
    object ClickCustomPeriod : ReportIntent()
}
