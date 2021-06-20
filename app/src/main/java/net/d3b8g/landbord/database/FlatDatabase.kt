package net.d3b8g.landbord.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FlatData::class], version = 1, exportSchema = false)
abstract class FlatDatabase : RoomDatabase() {

    abstract val flatDatabaseDao: FlatDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: FlatDatabase? = null

        fun getInstance(ct: Context): FlatDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        ct.applicationContext,
                        FlatDatabase::class.java,
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