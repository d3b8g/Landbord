package net.d3b8g.landbord.ui.notifications

data class NotificationsAdapterModel (
    val id: Int,
    val type: NotificationsDelayType,
    val date: String
    )

data class NotificationsAdapterModels (
    var notificationsList: List<NotificationsAdapterModel>
)