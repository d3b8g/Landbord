package net.d3b8g.landbord.components

import android.annotation.SuppressLint
import net.d3b8g.landbord.models.ParseDateModel
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

object Converter {

    @SuppressLint("SimpleDateFormat")
    fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd").format(Date())

    @SuppressLint("SimpleDateFormat")
    fun getTodayFullTime(): String = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())

    @SuppressLint("SimpleDateFormat")
    fun String.covertStringToDate(): Date = SimpleDateFormat("yyyy-MM-dd").parse(this)!!

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

    fun convertDate(month: Int, day: Int) = when (Locale.getDefault().displayLanguage) {
        "русский" -> "$day " + when (month) {
            1 -> "Января"
            2 -> "Февраля"
            3 -> "Марта"
            4 -> "Апреля"
            5 -> "Мая"
            6 -> "Июня"
            7 -> "Июля"
            8 -> "Августа"
            9 -> "Сентрября"
            10 -> "Октября"
            11 -> "Ноября"
            12 -> "Декабря"
            else -> "Января"
        }
        else -> "$day of ${DateFormatSymbols().months[month - 1]}"
    }

}