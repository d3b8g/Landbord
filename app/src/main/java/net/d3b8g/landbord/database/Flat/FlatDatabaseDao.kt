package net.d3b8g.landbord.database.Flat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FlatDatabaseDao {
    @Insert
    fun insert(flat: FlatData)

    @Update
    fun update(flat: FlatData)

    @Query("SELECT * from user_flat_info WHERE flatId = :key")
    fun get(key: Int): FlatData

    @Query("DELETE FROM user_flat_info")
    fun clear()

    @Query("SELECT * FROM user_flat_info ORDER BY flatId DESC")
    fun getAllFlats(): List<FlatData>

    @Query("SELECT flat_name FROM user_flat_info ORDER BY flatId DESC")
    fun getAllFlatsTitle(): List<String>
}

