package net.d3b8g.landbord

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.database.Booking.BookingData
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            //removeDB()
            //insertBooking()
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        this.apply {
            //createNotificationChannel()
            //sendNotification()
        }


        PreferenceManager.getDefaultSharedPreferences(this).apply {
                if (!getBoolean("have_flats", false)) {
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_add)
                    navView.visibility = View.GONE
                }
                if (!getBoolean("check_list_visible", true)) {
                    navView.menu.findItem(R.id.navigation_checklist).isVisible = false
                }
            }
    }

    override fun onBackPressed() {
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if (getBoolean("have_flats", false)) {
                super.onBackPressed()
            } else {
                Snackbar.make(binding.root, R.string.no_data_register, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.close_app) {
                        finishAffinity()
                    }
                    .show()
            }
        }
    }

    override fun onDestroy() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val flatBase = FlatDatabase.getInstance(applicationContext).flatDatabaseDao
                val bookingBase = BookingDatabase.getInstance(applicationContext).bookedDatabaseDao

                flatBase.deleteAll()
                bookingBase.deleteAll()
            }
        }
        super.onDestroy()
    }

    suspend fun removeDB() = withContext(Dispatchers.IO) {
        val db = FlatDatabase.getInstance(applicationContext).flatDatabaseDao
        db.deleteAll()
    }

    suspend fun insertBooking() = withContext(Dispatchers.IO) {
        val db = BookingDatabase.getInstance(applicationContext).bookedDatabaseDao
        db.insert(BookingData(
            id = 0,
            flatId = 0,
            bookingDate = "2021-8-4",
            deposit = 3540,
            username = "Pavel Milkov",
            userPhone = 89062188832,
            bookingEnd = "2021-08-04",
            bookingChatLink = ""
        ))
    }
}