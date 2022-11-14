package com.derleymad.myapplication.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavDao {
    @Insert
    fun insert(fav : FavTicket)

    @Delete
    fun delete(fav : FavTicket)

    @Query("SELECT * FROM FavTicket")
    fun getAllRegister() : List<FavTicket>

    @Query("SELECT * FROM FavTicket WHERE id = :id")
    fun getRegisterById(id: String) : FavTicket
}