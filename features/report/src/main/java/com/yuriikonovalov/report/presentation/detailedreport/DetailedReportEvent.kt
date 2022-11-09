package com.yuriikonovalov.report.presentation.detailedreport

import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.account.Account

sealed class DetailedReportEvent {
    data class ClickPeriodFilter(val period: PeriodFilter) : DetailedReportEvent()
    data class ClickAccountFilter(val accounts: List<Account>, val accountFilter: List<Account>) :
        DetailedReportEvent()

    data class ClickCustomPeriod(val startDate: Long?, val endDate: Long?) : DetailedReportEvent()
    data class ClickIncomeCurrencyFilter(val currencies: List<Currency>) : DetailedReportEvent()
    data class ClickExpenseCurrencyFilter(val currencies: List<Currency>) : DetailedReportEvent()
    data class ClickIncomeAndExpenseCurrencyFilter(val currencies: List<Currency>) :
        DetailedReportEvent()
}
