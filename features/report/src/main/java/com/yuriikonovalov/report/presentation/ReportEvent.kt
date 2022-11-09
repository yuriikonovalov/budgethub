package com.yuriikonovalov.report.presentation

import com.yuriikonovalov.common.application.entities.PeriodFilter

sealed class ReportEvent {
    data class CheckInitialPeriod(val periodFilter: PeriodFilter) : ReportEvent()
    data class ClickReportButton(val periodFilter: PeriodFilter) : ReportEvent()
    data class ClickSeeAllButton(val periodFilter: PeriodFilter) : ReportEvent()
    data class ClickCustomPeriod(val startDate: Long?, val endDate: Long?) : ReportEvent()
}
