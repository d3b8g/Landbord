package net.d3b8g.landbord.widgets

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter
import net.d3b8g.landbord.components.Converter.convertDate
import net.d3b8g.landbord.components.Converter.convertStringToDate
import net.d3b8g.landbord.components.Converter.parseDateToModel
import net.d3b8g.landbord.components.DateHelper
import net.d3b8g.landbord.components.appLog
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Flat.FlatDatabase
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class ReservedWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        val tenantNumber = intent.extras?.getString("TENANT_NUMBER")
        if (intent.action == "CALL_TENANT" &&
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intentCall = Intent(Intent.ACTION_CALL, Uri.parse("tel:$tenantNumber")).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intentCall, null)
        } else {
            val clipboardManager = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("TENANT_NUMBER", tenantNumber))
            Toast.makeText(context, context.getString(R.string.copy_number_to_clipboard), Toast.LENGTH_LONG).show()
        }
    }

    override fun onEnabled(context: Context) {}

    override fun onDisabled(context: Context) {}
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    var headerText = context.getString(R.string.notification_havent_today_record)
    var dateContext: String

    val dbBooking = BookingDatabase.getInstance(context).bookedDatabaseDao
    val dbFlat = FlatDatabase.getInstance(context).flatDatabaseDao
    val pendingIntentFlag = if (Build.VERSION.SDK_INT > 30) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    val today = Converter.getTodayDate()

    val views = RemoteViews(context.packageName, R.layout.reserved_widget)

    CoroutineScope(Dispatchers.IO).launch {
        dbBooking.getListByMonth("${today.dropLast(2)}01", "${today.dropLast(2)}31")?.let { dbBooking ->
            dbBooking.find { it.id == DateHelper.getCloserDate(dbBooking, today.convertStringToDate()) }?.let {
                headerText = "${dbFlat.get(it.flatId).flatName} ${context.getString(R.string.flat_busy_by)} ${it.username}"
                val rentDate = parseDateToModel(it.bookingEnd)
                dateContext = "${context.getString(R.string.to)} ${convertDate(rentDate.month.toInt(), rentDate.day.toInt())}"

                val selfPendingIntent = PendingIntent.getBroadcast(
                    context,
                    13,
                    Intent(context, ReservedWidget::class.java).apply {
                        action = "CALL_TENANT"
                        putExtra("TENANT_NUMBER", it.userPhone.toString())
                    },
                    pendingIntentFlag
                )

                withContext(Dispatchers.Main) {
                    views.apply {
                        setTextViewText(R.id.appwidget_header, headerText + dateContext)
                        setViewVisibility(R.id.appwidget_call_user, View.VISIBLE)
                        setOnClickPendingIntent(R.id.appwidget_call_user, selfPendingIntent)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }

    views.setTextViewText(R.id.appwidget_header, headerText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}