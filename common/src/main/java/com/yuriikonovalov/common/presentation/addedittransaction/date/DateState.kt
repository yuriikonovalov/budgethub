package com.yuriikonovalov.common.presentation.addedittransaction.date

import java.time.OffsetDateTime

data class DateState(
    val minDate: OffsetDateTime? = null,
    val maxDate: OffsetDateTime = OffsetDateTime.now(),
    val date: OffsetDateTime = OffsetDateTime.now()
) {
    fun updateDate(date: OffsetDateTime): DateState {
        return copy(date = date)
    }
}
