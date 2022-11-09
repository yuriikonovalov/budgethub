package com.yuriikonovalov.common.framework.common.extentions

import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.util.beginningOfMonth
import com.yuriikonovalov.common.application.util.beginningOfWeek
import com.yuriikonovalov.common.application.util.beginningOfYear
import java.time.OffsetDateTime

fun PeriodFilter.toStartEndDate(): Pair<OffsetDateTime?, OffsetDateTime?> {
    val now = OffsetDateTime.now()
    return when (this) {
        is PeriodFilter.Week -> Pair(now.beginningOfWeek(), null)
        is PeriodFilter.Month -> Pair(now.beginningOfMonth(), null)
        is PeriodFilter.Year -> Pair(now.beginningOfYear(), null)
        is PeriodFilter.All -> Pair(null, null)
        is PeriodFilter.Custom -> Pair(this.startPeriod, this.endPeriod)
    }
}