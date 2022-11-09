package com.yuriikonovalov.common.application.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

sealed class PeriodFilter : Parcelable {
    @Parcelize
    object Week : PeriodFilter()

    @Parcelize
    object Month : PeriodFilter()

    @Parcelize
    object Year : PeriodFilter()

    @Parcelize
    object All : PeriodFilter()

    @Parcelize
    data class Custom(val startPeriod: OffsetDateTime, val endPeriod: OffsetDateTime) :
        PeriodFilter()
}