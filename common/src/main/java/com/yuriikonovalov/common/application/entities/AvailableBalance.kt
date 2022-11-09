package com.yuriikonovalov.common.application.entities

data class AvailableBalance(
    val accountId: Long,
    val currency: Currency,
    val startBalance: Double,
    val endBalance: Double,
)