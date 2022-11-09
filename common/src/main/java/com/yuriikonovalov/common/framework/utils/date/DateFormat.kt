@file:Suppress("unused", "unused")

package com.yuriikonovalov.common.framework.utils.date


import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateFormat {
    /**
     * Converts given year, month and dayOfMonth by [android.widget.CalendarView]
     * into [OffsetDateTime].
     */
    fun offsetDateTimeFromCalendarViewDate(year: Int, month: Int, dayOfMonth: Int): OffsetDateTime {
        // CalendarView operates by the months as Integers in the range of 0 to 11.
        // OffsetDateTime uses a 1-12 range.
        val offsetDateTimeMonth = month + 1

        return OffsetDateTime.of(
            year,
            offsetDateTimeMonth,
            dayOfMonth,
            OffsetDateTime.now().hour,
            OffsetDateTime.now().minute,
            OffsetDateTime.now().second,
            OffsetDateTime.now().nano,
            OffsetDateTime.now().offset
        )
    }

    /**
     * Converts a given [OffsetDateTime] value to a [Long] value acceptable by [android.widget.CalendarView]
     * as a date in millis.
     */
    private fun offsetDateTimeToLongDate(offsetDateTime: OffsetDateTime): Long {
        val year = offsetDateTime.year
        // CalendarView operates by the months as Integers in the range of 0 to 11.
        // OffsetDateTime uses a 1-12 range.
        val month = offsetDateTime.monthValue - 1
        val day = offsetDateTime.dayOfMonth

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        return calendar.timeInMillis
    }

    // Extension functions
    fun OffsetDateTime.toCalendarViewDateLong(): Long {
        return offsetDateTimeToLongDate(this)
    }

    fun OffsetDateTime.toE(): String {
        return format(UI.FORMATTER_E)
    }

    fun OffsetDateTime.toDdMmYyyy(): String {
        return format(UI.FORMATTER_DD_MM_YYYY)
    }

    fun OffsetDateTime.toDdMmmmYyyy(): String {
        return format(UI.FORMATTER_DD_MMMM_YYYY)
    }

    fun OffsetDateTime.toEeeeDdMmmmYyyy(): String {
        return format(UI.FORMATTER_EEEE_DD_MMM_YYYY)
    }

    fun OffsetDateTime.toDdMmmYyyy(): String {
        return format(UI.FORMATTER_DD_MMM_YYYY)
    }


    fun OffsetDateTime.toMmmmYyyy(): String {
        return format(UI.FORMATTER_MMMM_YYYY)
    }

    object UI {
        val FORMATTER_E: DateTimeFormatter = DateTimeFormatter.ofPattern("E")
        val FORMATTER_DD_MM_YYYY: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val FORMATTER_MMMM_YYYY: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        val FORMATTER_DD_MMMM_YYYY: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val FORMATTER_DD_MMM_YYYY: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val FORMATTER_EEEE_DD_MMM_YYYY: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")
    }
}