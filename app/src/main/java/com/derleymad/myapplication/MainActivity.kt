package com.derleymad.myapplication

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.derleymad.myapplication.databinding.ActivityMainBinding
import com.derleymad.myapplication.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var doubleBackToExitPressedOnce = false

    val tickets = mutableListOf<List<Ticket>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
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

    override fun onDestroy() {
        val sharedPreferences = getSharedPreferences("credentials",Context.MODE_PRIVATE)
        val autologin = sharedPreferences.getBoolean("autologin",false)

        if (autologin){
            finish()
        }else{
            sharedPreferences.edit().remove("credentials").apply()
            Log.i("removidosp","removido")
        }
        super.onDestroy()
    }
}


