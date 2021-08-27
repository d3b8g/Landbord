package net.d3b8g.landbord.database.Booking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface BookingDatabaseDao {

    @Insert
    fun insert(data: BookingData)

    @Update
    fun update(data: BookingData)

    @Query("SELECT * from booking WHERE booking_date = :date LIMIT 1")
    fun getByDate(date: String): BookingData

    @Query("SELECT * from booking WHERE booking_date BETWEEN :dateStart AND :dateEnd")
    fun getListByDate(dateStart: Date, dateEnd: Date): List<BookingData>

}