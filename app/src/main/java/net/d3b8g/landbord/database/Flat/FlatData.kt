package net.d3b8g.landbord.database.Flat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_flat_info")
data class FlatData(
    @PrimaryKey(autoGenerate = true)
    val flatId: Int,

    @ColumnInfo(name = "flat_name")
    var flatName: String,

    @ColumnInfo(name = "flat_address")
    var flatAddress: String,

    @ColumnInfo(name = "flat_link")
    var flatLink: String?,

    @ColumnInfo(name = "flat_notes")
    var flatNotes: String?,


)