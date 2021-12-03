package net.d3b8g.landbord.database.Checklists

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import net.d3b8g.landbord.components.Converter

@Dao
interface CheckListDatabaseDao {

    @Query("DELETE FROM checklists")
    fun deleteAll()

    @Insert
    fun insert(data: CheckListData): Long

    @Update(entity = CheckListData::class)
    fun update(data: CheckListData)

    @Query("SELECT id from checklists WHERE clTitle = :item")
    fun selectSimilarItem(item: String): Int

    @Query("SELECT * from checklists")
    fun getAllItems(): List<CheckListData>

    @Query("DELETE from checklists WHERE id = :id")
    fun deleteCurrentId(id: Int)

    @Query("SELECT * from checklists WHERE reminderDate = :date")
    fun getTodayList(date: String): List<CheckListData>

    @Query("SELECT * from checklists WHERE reminderDate BETWEEN :dateStart AND :dateEnd")
    fun getThisMonthItems(dateStart: String, dateEnd: String): List<CheckListData>
}