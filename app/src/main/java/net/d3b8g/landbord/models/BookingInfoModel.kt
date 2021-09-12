package net.d3b8g.landbord.models

data class BookingInfoModel (
    val date: String,
    val bookedBy: String,
    val phone: Long,
    val deposit: Int
)