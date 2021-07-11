package net.d3b8g.landbord

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.database.Flat.FlatDatabase
import net.d3b8g.landbord.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        lifecycleScope.launch {
//            //removeDB()
//            testInsert()
//        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if(!getBoolean("have_flats", false)) {
                findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_add)
                navView.visibility = View.GONE
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

    suspend fun removeDB() = withContext(Dispatchers.IO) {
        val db = FlatDatabase.getInstance(applicationContext).flatDatabaseDao
        db.clear()
    }


}