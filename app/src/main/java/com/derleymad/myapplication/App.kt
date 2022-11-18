package com.derleymad.myapplication

import android.app.Application
import com.derleymad.myapplication.model.AppDataBase

class App : Application(){

    lateinit var  db : AppDataBase

    override fun onCreate() {
        super.onCreate()
        db = AppDataBase.getDataBase(this@App)

        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

    }
}