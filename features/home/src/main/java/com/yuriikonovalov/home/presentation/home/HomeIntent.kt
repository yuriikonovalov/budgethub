package com.yuriikonovalov.home.presentation.home

sealed class HomeIntent {
    data class OpenTransactionDetails(val id: Long) : HomeIntent()
    data class OpenTransferDetails(val id: Long) : HomeIntent()
    object ClickAvailableBalancePercent : HomeIntent()
}
