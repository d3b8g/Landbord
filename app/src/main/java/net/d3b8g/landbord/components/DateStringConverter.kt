package net.d3b8g.landbord.components

class DateStringConverter {
    companion object {

        fun convertDateToPattern(date: String) =
            date.split("-").joinToString("-") { item -> if (item.length == 1) "0$item" else item }

    }
}