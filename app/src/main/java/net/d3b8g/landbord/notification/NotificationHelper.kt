package net.d3b8g.landbord.notification

import android.content.Context
import android.content.Intent
import android.os.Build

object NotificationHelper {
    fun Context.setupBroadcastNotification(action: NotificationsActions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == NotificationsActions.STOP) return

        Intent(this, NotificationService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
            } else {
                startService(it)
            }
        }
    }
}