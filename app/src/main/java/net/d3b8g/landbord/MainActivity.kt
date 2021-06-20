package net.d3b8g.landbord

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.d3b8g.landbord.database.FlatData
import net.d3b8g.landbord.database.FlatDatabase
import net.d3b8g.landbord.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        lifecycleScope.launch {
//            testInsert()
//        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

    }

    suspend fun testInsert() = withContext(Dispatchers.IO) {
        val db = FlatDatabase.getInstance(applicationContext).flatDatabaseDao
        db.insert(FlatData(
            flatId = 0,
            flatName = "TestData_Flat_0",
            flatSummary = "Same about this flat in test data"
        ))
    }
}