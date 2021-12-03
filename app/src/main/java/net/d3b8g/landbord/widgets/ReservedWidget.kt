package net.d3b8g.landbord.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
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

/**
 * Implementation of App Widget functionality.
 */
class ReservedWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    var headerText = context.getString(R.string.notification_havent_today_record)
    var dateContext: String

    val dbBooking = BookingDatabase.getInstance(context).bookedDatabaseDao
    val dbFlat = FlatDatabase.getInstance(context).flatDatabaseDao
    val today = Converter.getTodayDate()

    val views = RemoteViews(context.packageName, R.layout.reserved_widget)

    CoroutineScope(Dispatchers.IO).launch {
        dbBooking.getListByMonth("${today.dropLast(2)}01", "${today.dropLast(2)}31")?.let { dbBooking ->
            dbBooking.find { it.id == DateHelper.getCloserDate(dbBooking, today.convertStringToDate()) }?.let {
                headerText = "${dbFlat.get(it.flatId).flatName} ${context.getString(R.string.flat_busy_by)} ${it.username}"
                val rentDate = parseDateToModel(it.bookingEnd)
                dateContext = "${context.getString(R.string.to)} ${convertDate(rentDate.month.toInt(), rentDate.day.toInt())}"

                withContext(Dispatchers.Main) {
                    views.apply {
                        setTextViewText(R.id.appwidget_header, headerText)
                        setViewVisibility(R.id.appwidget_context, View.VISIBLE)
                        setTextViewText(R.id.appwidget_context, dateContext)
                    }
                }
            }
        }
    }

    views.setTextViewText(R.id.appwidget_header, headerText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}