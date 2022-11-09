package com.yuriikonovalov.common.framework.data.local.database.converter

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class DateConverters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toOffsetDateTime(value: String): OffsetDateTime {
        return formatter.parse(value, OffsetDateTime::from)
    }

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime): String {
        return date.format(formatter)
    }
}