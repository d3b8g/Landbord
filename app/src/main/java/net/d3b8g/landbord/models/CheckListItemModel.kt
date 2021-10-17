package net.d3b8g.landbord.models

data class CheckListItemModel (
    val title: String,
    val isCompleted: Boolean,
    val isRepeatable: Boolean,
    val nextRepeat: String?
    )