package com.yuriikonovalov.common.application.util

import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*


enum class PartOfDay {
    MORNING, AFTERNOON, EVENING, NIGHT
}

fun OffsetDateTime.beginningOfDay(): OffsetDateTime {
    return this.withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
}

fun OffsetDateTime.endOfDay(): OffsetDateTime {
    return this.withHour(23)
        .withMinute(59)
        .withSecond(59)
        .withNano(999999999)
}

fun OffsetDateTime.beginningOfWeek(): OffsetDateTime {
    // Number of days from the beginning of the week.
    val daysFromBeginning = when (this.dayOfWeek!!) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }
    val resetTimeToBeginning = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 0, 0, 0, 0, this.offset
    )
    return resetTimeToBeginning.minusDays(daysFromBeginning.toLong())
}

fun OffsetDateTime.endOfWeek(): OffsetDateTime {
    // Number of days to the end of the week.
    val daysToEnd = when (this.dayOfWeek!!) {
        DayOfWeek.MONDAY -> 6
        DayOfWeek.TUESDAY -> 5
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 2
        DayOfWeek.SATURDAY -> 1
        DayOfWeek.SUNDAY -> 0
    }
    val setTimeToEnd = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 23, 59, 59, 999999999, this.offset
    )
    return setTimeToEnd.plusDays(daysToEnd.toLong())
}

fun OffsetDateTime.beginningOfYear(): OffsetDateTime {
    return OffsetDateTime.of(
        this.year, 1, 1, 0, 0, 0, 0, this.offset
    )
}

fun OffsetDateTime.beginningOfMonth(): OffsetDateTime {
    return OffsetDateTime.of(
        this.year, this.monthValue, 1, 0, 0, 0, 0, this.offset
    )
}

fun OffsetDateTime.endOfMonth(): OffsetDateTime {
    val rawDate = OffsetDateTime.of(
        this.year, this.monthValue, 1, 23, 59, 59, 999999999, this.offset
    )
    /* 'withDayOfMonth()'throws DateTimeException if the day-of-month value is invalid,
        or if the day-of-month is invalid for the month-year.
        Try set the last day of month from bigger to smaller.
     */
    return try {
        // January, March, May, July, August, October, December
        rawDate.withDayOfMonth(31)
    } catch (e: DateTimeException) {
        try {
            // April, June, September, November
            rawDate.withDayOfMonth(30)
        } catch (e: DateTimeException) {
            try {
                // February
                rawDate.withDayOfMonth(29)
            } catch (e: DateTimeException) {
                // February
                rawDate.withDayOfMonth(28)
            }
        }
    }
}

fun OffsetDateTime.nightRange(): Pair<OffsetDateTime, OffsetDateTime> {
    val start = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 0, 0, 0, 0, this.offset
    )
    val end = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 4, 59, 59, 999999999, this.offset
    )
    return Pair(start, end)
}

fun OffsetDateTime.morningRange(): Pair<OffsetDateTime, OffsetDateTime> {
    val start = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 5, 0, 0, 0, this.offset
    )
    val end = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 11, 59, 59, 999999999, this.offset
    )
    return Pair(start, end)
}

fun OffsetDateTime.afternoonRange(): Pair<OffsetDateTime, OffsetDateTime> {
    val start = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 12, 0, 0, 0, this.offset
    )
    val end = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 16, 59, 59, 999999999, this.offset
    )
    return Pair(start, end)
}

fun OffsetDateTime.eveningRange(): Pair<OffsetDateTime, OffsetDateTime> {
    val start = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 17, 0, 0, 0, this.offset
    )
    val end = OffsetDateTime.of(
        this.year, this.monthValue, this.dayOfMonth, 23, 59, 59, 999999999, this.offset
    )
    return Pair(start, end)
}


fun OffsetDateTime.isPartOfDay(partOfDay: PartOfDay): Boolean {
    return when (partOfDay) {
        PartOfDay.MORNING -> {
            val from = morningRange().first
            val to = morningRange().second
            this in from..to
        }
        PartOfDay.AFTERNOON -> {
            val from = afternoonRange().first
            val to = afternoonRange().second
            this in from..to
        }
        PartOfDay.EVENING -> {
            val from = eveningRange().first
            val to = eveningRange().second
            this in from..to
        }
        PartOfDay.NIGHT -> {
            val from = nightRange().first
            val to = nightRange().second
            this in from..to
        }
    }
}

fun Long.toDateOffsetDateTime(): OffsetDateTime {
    val instant = Date(this).toInstant()
    return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault())
}
