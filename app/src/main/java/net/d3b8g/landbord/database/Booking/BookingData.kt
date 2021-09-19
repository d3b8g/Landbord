package net.d3b8g.landbord.database.Booking

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking")
data class BookingData(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "flatId")
    val flatId: Int,

    @ColumnInfo(name = "booking_date")
    val bookingDate: String,

    @ColumnInfo(name = "deposit")
    val deposit: Int,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "user_phone")
    val userPhone: Long,

    @ColumnInfo(name = "booking_end")
    val bookingEnd: String,

    @ColumnInfo(name = "chat_link")
    val bookingChatLink: String
)