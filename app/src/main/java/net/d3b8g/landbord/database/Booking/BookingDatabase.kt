package net.d3b8g.landbord.database.Booking

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.d3b8g.landbord.components.Converters
import net.d3b8g.landbord.database.Flat.FlatData

@Database(entities = [BookingData::class, FlatData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BookingDatabase : RoomDatabase() {

    abstract val bookedDatabaseDao: BookingDatabaseDao


    companion object {
        @Volatile
        private var INSTANCE: BookingDatabase? = null

        fun getInstance(ct: Context): BookingDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        ct.applicationContext,
                        BookingDatabase::class.java,
                        "flat_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}