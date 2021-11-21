package net.d3b8g.landbord.database.Checklists

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CheckListDatabaseDao {

    @Query("DELETE FROM checklists")
    fun deleteAll()

    @Insert
    fun insert(data: CheckListData): Long

    @Update
    fun update(data: CheckListData)

    @Query("SELECT id from checklists WHERE clTitle = :item")
    fun selectSimilarItem(item: String): Int
}