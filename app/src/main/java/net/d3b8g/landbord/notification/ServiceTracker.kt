package net.d3b8g.landbord.notification

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import net.d3b8g.landbord.ui.notifications.NotificationsAdapterModel
import net.d3b8g.landbord.ui.notifications.NotificationsAdapterModels


private const val notificationDelayedAt = "HOURS_NOTIFICATION_DELAY"
private const val notificationsJsonElement = "NOTIFICATIONS_JSON"
private const val notificationsStatus = "NOTIFICATIONS_STATUS"

fun setNotificationStatus(context: Context, isEnable: Boolean) {
    getPreferences(context).edit {
        putBoolean(notificationsStatus, isEnable)
    }
}

fun getNotificationStatus(context: Context) = getPreferences(context).getBoolean(notificationsStatus, true)

fun setNotificationsJson(context: Context, addNotification: NotificationsAdapterModel) {
    val getActualNotificationList = getNotificationsJson(context)

    val gsonNotificationsList: String = if (getActualNotificationList != null) {
        if (getActualNotificationList.notificationsList.size == 10) {
            getActualNotificationList.notificationsList.dropLast(0)
        }
        appLog(context, "Add new: $addNotification")
        getActualNotificationList.notificationsList.add(0, addNotification)
        getActualNotificationList.notificationsList.mapIndexed { index , notificationsAdapterModel ->
            if (index > 0) notificationsAdapterModel.id = index
        }

        Gson().toJson(getActualNotificationList, NotificationsAdapterModels::class.java)
    } else {
        Gson().toJson(NotificationsAdapterModels(arrayListOf(addNotification)), NotificationsAdapterModels::class.java)
    }

    appLog(context, gsonNotificationsList)
    getPreferences(context).edit {
        putString(notificationsJsonElement, gsonNotificationsList)
    }
}

fun getNotificationsJson(context: Context): NotificationsAdapterModels? {
    getPreferences(context).getString(notificationsJsonElement, "")!!.also {
        return if (it.isNotEmpty()) {
            Log.e("PrefManager", it)
            Gson().fromJson(it, NotificationsAdapterModels::class.java)
        } else null
    }
}

fun setNotificationDelay(context: Context, time: String) {
    getPreferences(context).edit {
        Log.e("PrefManager", time)
        putString(notificationDelayedAt, time)
    }
}

fun getNotificationDelay(context: Context): String = getPreferences(context).getString(notificationDelayedAt, "10:00")!!

private fun getPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

fun appLog(fromClass: Any, text: String) {
    Log.e(fromClass::class.java.simpleName, text)
}
