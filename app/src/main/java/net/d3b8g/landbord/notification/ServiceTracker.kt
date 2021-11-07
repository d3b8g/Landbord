package net.d3b8g.landbord.notification

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import net.d3b8g.landbord.ui.notifications.NotificationsAdapterModels

enum class ServiceState {
    STARTED,
    STOPPED,
}

private const val key = "NOTIFICATION_STATE"
private const val notificationDelayedAt = "HOURS_NOTIFICATION_DELAY"
private const val notificationsJsonElement = "NOTIFICATIONS_JSON"
private const val notificationsStatus = "NOTIFICATIONS_STATUS"

fun setNotificationStatus(context: Context, isEnable: Boolean) {
    getPreferences(context).edit {
        putBoolean(notificationsStatus, isEnable)
    }
}

fun getNotificationStatus(context: Context) = getPreferences(context).getBoolean(notificationsStatus, true)

fun setNotificationsJson(context: Context, json: String) {
    getPreferences(context).edit {
        putString(notificationsJsonElement, json)
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

fun setServiceState(context: Context, state: ServiceState) {
    getPreferences(context).edit {
        putString(key, state.name)
    }
}

fun getServiceState(context: Context): ServiceState {
    val sharedPrefs = getPreferences(context)
    val value = sharedPrefs.getString(key, ServiceState.STOPPED.name)
    return ServiceState.valueOf(value!!)
}

private fun getPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

fun log(fromClass: Any, text: String) {
    Log.e(fromClass::class.java.simpleName, text)
}
