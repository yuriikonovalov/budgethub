package com.yuriikonovalov.home.presentation.home

sealed class HomeEvent {
    data class OpenTransactionDetails(val id: Long) : HomeEvent()
    data class OpenTransferDetails(val id: Long) : HomeEvent()
    object ClickAvailableBalancePercent : HomeEvent()
}
