package net.d3b8g.landbord.models

data class BookingInfoModel (
    val id: Int,
    val date: String,
    val bookedBy: String,
    val phone: Long,
    val deposit: Int
)