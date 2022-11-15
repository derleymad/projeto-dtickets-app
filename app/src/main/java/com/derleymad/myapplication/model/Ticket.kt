package com.derleymad.myapplication.model

data class Ticket(
    val isfixed : Boolean = false,
    val type : String,
    val size : String,
    val id : String = "",
    val email : String = "",
    val numero : String = "",
    val assunto : String = "",
    val data : String = "",
    val de : String = "",
    val para : String = "",
    val prioridade : String = ""
)
