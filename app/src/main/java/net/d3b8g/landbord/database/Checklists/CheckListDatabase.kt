package net.d3b8g.landbord.database.Checklists

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Flat.FlatData

@Database(entities = [BookingData::class, FlatData::class, CheckListData::class], version = 1, exportSchema = false)
abstract class CheckListDatabase : RoomDatabase() {

    abstract val checkListDatabaseDao: CheckListDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: CheckListDatabase? = null

        fun getInstance(ct: Context): CheckListDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        ct.applicationContext,
                        CheckListDatabase::class.java,
                        "landlord13_database"
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