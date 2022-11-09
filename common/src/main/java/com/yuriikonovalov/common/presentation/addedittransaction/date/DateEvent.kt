package com.yuriikonovalov.common.presentation.addedittransaction.date

import java.time.OffsetDateTime

sealed class DateEvent {
    data class PositiveButtonClick(val date: OffsetDateTime) : DateEvent()
}
