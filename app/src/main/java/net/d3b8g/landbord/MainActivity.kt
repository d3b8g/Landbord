package net.d3b8g.landbord

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
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
import net.d3b8g.landbord.components.getNotificationStatus
import net.d3b8g.landbord.database.Booking.BookingDatabase
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.ActivityMainBinding
import net.d3b8g.landbord.notification.NotificationHelper.delayedNotificationAlarm
import net.d3b8g.landbord.payments.PaymentsDetails
import net.d3b8g.landbord.ui.add.AddViewModel
import net.d3b8g.landbord.ui.add.AddViewState

class MainActivity : AppCompatActivity() {

    private val addViewModel: AddViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        //Send Notification
        if (getNotificationStatus(this)) this.delayedNotificationAlarm()

        //Init preferences fragments
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if (!getBoolean("have_flats", false)) {
                addViewModel.state.value = AddViewState.NEW_USER
                findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_add)
                navView.visibility = View.GONE
            }
            if (!getBoolean("check_list_visible", true)) {
                navView.menu.findItem(R.id.navigation_checklist).isVisible = false
            }
        }

        addViewModel.tabbarHide.observe(this, {
            if (it) navView.visibility = View.GONE
            else navView.visibility = View.VISIBLE
        })
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
        PaymentsDetails.billingProcessor?.release()
        super.onDestroy()
    }

}