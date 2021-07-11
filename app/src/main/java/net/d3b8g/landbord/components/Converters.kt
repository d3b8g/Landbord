package net.d3b8g.landbord.components

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.*

@ProvidedTypeConverter
class Converters {
    companion object {

        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

        @TypeConverter
        @JvmStatic
        fun dateToTimestamp(date: Date?): Long? = date?.time
    }
}