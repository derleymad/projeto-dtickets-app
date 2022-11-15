package com.derleymad.myapplication

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.derleymad.myapplication.databinding.ActivityMainBinding
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.ui.fragments.navigation.BaseFragment
import com.derleymad.myapplication.ui.fragments.navigation.DashboardFragment
import com.derleymad.myapplication.ui.fragments.navigation.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var doubleBackToExitPressedOnce = false
    private lateinit var username : String
    private lateinit var password : String

    val tickets = mutableListOf<List<Ticket>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference =  getSharedPreferences("credentials", Context.MODE_PRIVATE)

        username = sharedPreference.getString("username","none").toString()
        password = sharedPreference.getString("password","none").toString()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_base, R.id.navigation_dashboard, R.id.navigation_settings
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
       Snackbar.make(binding.root, "Pressione novamente para sair!", Snackbar.LENGTH_SHORT)
           .show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}

