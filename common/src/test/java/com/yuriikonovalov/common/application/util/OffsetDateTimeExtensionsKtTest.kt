package com.yuriikonovalov.common.application.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.OffsetDateTime

class OffsetDateTimeExtensionsKtTest {
    @Test
    fun `OffsetDateTime to beginning of day`() {
        // BEFORE
        val now = OffsetDateTime.now()

        // WHEN
        val beginningOfDay = now.beginningOfDay()

        // THEN
        // Day is not changed.
        assertEquals(now.dayOfMonth, beginningOfDay.dayOfMonth)
        // Units of time are set to 0.
        assertEquals(0, beginningOfDay.hour)
        assertEquals(0, beginningOfDay.minute)
        assertEquals(0, beginningOfDay.second)
        assertEquals(0, beginningOfDay.nano)
    }

    @Test
    fun `OffsetDateTime to beginning of week`() {
        // BEFORE
        val now = OffsetDateTime.now()

        // WHEN
        val beginningOfWeek = now.beginningOfWeek()

        // THEN
        // Day of week is Monday (the first day of the week).
        assertEquals(DayOfWeek.MONDAY, beginningOfWeek.dayOfWeek)
        // Units of time are set to 0.
        assertEquals(0, beginningOfWeek.hour)
        assertEquals(0, beginningOfWeek.minute)
        assertEquals(0, beginningOfWeek.second)
        assertEquals(0, beginningOfWeek.nano)
        // Date is set to the nearest first day of the week.
        // Beginning of week date should not be before then the 'now' date minus 1 week.
        val weekBeforeWithTrimmedTime = now.minusWeeks(7)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        val before = beginningOfWeek.isBefore(weekBeforeWithTrimmedTime)
        assertEquals(false, before)
    }

    @Test
    fun `OffsetDateTime to end of month`() {
        // BEFORE
        val dateInFebruary = OffsetDateTime.now().withMonth(2)
        val dateInApril = OffsetDateTime.now().withMonth(4)
        val dateInOctober = OffsetDateTime.now().withMonth(10)

        // WHEN
        val endOfFebruary = dateInFebruary.endOfMonth()
        val endOfApril = dateInApril.endOfMonth()
        val endOfOctober = dateInOctober.endOfMonth()

        // THEN
        // The end of month means: a day of month and date units set to max values.
        assertEquals(23, endOfFebruary.hour)
        assertEquals(59, endOfFebruary.minute)
        assertEquals(59, endOfFebruary.second)
        assertEquals(999999999, endOfFebruary.nano)

        assertEquals(23, endOfApril.hour)
        assertEquals(59, endOfApril.minute)
        assertEquals(59, endOfApril.second)
        assertEquals(999999999, endOfApril.nano)

        assertEquals(23, endOfOctober.hour)
        assertEquals(59, endOfOctober.minute)
        assertEquals(59, endOfOctober.second)
        assertEquals(999999999, endOfOctober.nano)
        // The last day of February can be 28 or 29.
        // For January, March, May, July, August, October, December - 31.
        // For April, June, September, November - 30.
        assertEquals(true, endOfFebruary.dayOfMonth == 28 || endOfFebruary.dayOfMonth == 29)
        assertEquals(30, endOfApril.dayOfMonth)
        assertEquals(31, endOfOctober.dayOfMonth)
    }

    @Test
    fun `OffsetDateTime is part of day`() {
        // BEFORE
        val morning = OffsetDateTime.now().withHour(9)
        val afternoon = OffsetDateTime.now().withHour(15)
        val evening = OffsetDateTime.now().withHour(22)
        val night = OffsetDateTime.now().withHour(2)

        // WHEN
        val isMorning = morning.isPartOfDay(PartOfDay.MORNING)
        val isAfternoon = afternoon.isPartOfDay(PartOfDay.AFTERNOON)
        val isEvening = evening.isPartOfDay(PartOfDay.EVENING)
        val isNight = night.isPartOfDay(PartOfDay.NIGHT)
        val isNightWrong = morning.isPartOfDay(PartOfDay.NIGHT)

        // THEN
        // True if the date is between 05:00:00:00 and 11:59:59:999999999
        assertEquals(true, isMorning)
        // True if the date is between 12:00:00:00 and 16:59:59:999999999
        assertEquals(true, isAfternoon)
        // True if the date is between 17:00:00:00 and 23:59:59:999999999
        assertEquals(true, isEvening)
        // True if the date is between 00:00:00:00 and 04:59:59:999999999
        assertEquals(true, isNight)

        assertEquals(false, isNightWrong)
    }
}