package net.d3b8g.landbord.database.Booking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookingDatabaseDao {

    @Query("DELETE FROM booking")
    fun deleteAll()

    @Insert
    fun insert(data: BookingData): Long

    @Update
    fun update(data: BookingData)

    @Query("SELECT count(*) from booking")
    fun countBooking(): Int

    @Query("SELECT * from booking WHERE id = :id")
    fun getById(id: Int): BookingData

    @Query("SELECT * from booking WHERE booking_date = :date LIMIT 1")
    fun getByDate(date: String): BookingData

    @Query("SELECT * from booking WHERE booking_date BETWEEN :dateStart AND :dateEnd OR booking_end BETWEEN :dateStart AND :dateEnd")
    fun getListByDate(dateStart: String, dateEnd: String): List<BookingData>?

    @Query("SELECT * from booking WHERE booking_date BETWEEN :dateStart AND :dateEnd OR booking_end BETWEEN :dateStart AND :dateEnd")
    fun getListByMonth(dateStart: String, dateEnd: String): List<BookingData>?

    @Query("DELETE from booking WHERE id = :id")
    fun deleteBookingUser(id: Int)

}