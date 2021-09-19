package net.d3b8g.landbord.components

import android.annotation.SuppressLint
import net.d3b8g.landbord.models.ParseDateModel
import java.text.SimpleDateFormat
import java.util.*

object Converter {

    @SuppressLint("SimpleDateFormat")
    fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd").format(Date())

    fun getTodayUnix(): Long = System.currentTimeMillis()

    fun convertDateToPattern(date: String): String =
        date.split("-").joinToString("-") { item -> if (item.length == 1) "0$item" else item }

    @SuppressLint("SimpleDateFormat")
    fun convertUnixToDate(date: Long): String = SimpleDateFormat("yyyy-MM-dd").format(date)

    // Also can use parser at LocalDate.parse(string, DateTimeFormatter.ISO_DATE).dayOf -- ONLY API > 26 API
    fun parseDateToModel(date: String) = ParseDateModel(
        day = date.takeLast(2),
        month = date.substring(5,7),
        year = date.take(4)
    )
}