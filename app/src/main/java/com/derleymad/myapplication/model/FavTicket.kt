package com.derleymad.myapplication.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavTicket(
    @PrimaryKey(autoGenerate = false) val id : String,
    @ColumnInfo(name = "isfixed") val isfixed : Boolean = true,
    @ColumnInfo(name = "myname") val myName : String,
    @ColumnInfo(name = "type") val type : String,
    @ColumnInfo(name = "de") val de : String,
    @ColumnInfo(name = "para") val para: String,
    @ColumnInfo(name = "descricao") val descricao : String,
    @ColumnInfo(name = "status") val status : String,
    @ColumnInfo(name = "prioridade") val prioridade : String,
    @ColumnInfo(name = "setor") val setor : String,
    @ColumnInfo(name = "dataCriacao") val dataCriacao : String,
    @ColumnInfo(name = "email") val email : String,
    @ColumnInfo(name = "numeroTicket") val numeroTicket : String,
)
