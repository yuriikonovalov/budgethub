package com.yuriikonovalov.common.presentation.addedittransaction.date

import java.time.OffsetDateTime

sealed class DateIntent {
    data class ChangeDate(val date: OffsetDateTime) : DateIntent()
    object ClickPositiveButton : DateIntent()
}
