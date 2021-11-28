package net.d3b8g.landbord.database.Checklists

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.d3b8g.landbord.components.Converter.getTodayDate

@Entity(tableName = "checklists")
data class CheckListData (

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "clTitle")
    val title: String,

    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "isRepeatable")
    val isRepeatable: Boolean = true,

    @ColumnInfo(name = "reminderDate")
    val reminderDate: String = getTodayDate()
)