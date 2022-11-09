package com.yuriikonovalov.report.presentation.detailedreport

import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.account.Account

sealed class DetailedReportIntent {
    data class ChangePeriodFilter(val periodFilter: PeriodFilter) : DetailedReportIntent()
    data class ChangeAccountFilter(val accounts: List<Account>) : DetailedReportIntent()
    data class ChangeIncomeCurrencyFilter(val currency: Currency) : DetailedReportIntent()
    data class ChangeExpenseCurrencyFilter(val currency: Currency) : DetailedReportIntent()
    data class ChangeIncomeAndExpenseCurrencyFilter(val currency: Currency) : DetailedReportIntent()

    object ClickCustomPeriod : DetailedReportIntent()
    object ClickPeriodFilter : DetailedReportIntent()
    object ClickAccountFilter : DetailedReportIntent()
    object ClickIncomeCurrencyFilter : DetailedReportIntent()
    object ClickExpenseCurrencyFilter : DetailedReportIntent()
    object ClickIncomeAndExpenseCurrencyFilter : DetailedReportIntent()
}
