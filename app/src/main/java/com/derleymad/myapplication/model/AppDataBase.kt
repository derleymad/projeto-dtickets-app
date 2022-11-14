package com.derleymad.myapplication.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FavTicket::class], version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun TicketDao() : FavDao

    companion object{
        private var INSTANCE : AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase{
            return if(INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "fav_db"
                    ).build()
                }
                INSTANCE as AppDataBase
            }else{
                INSTANCE as AppDataBase
            }

        }
    }
}