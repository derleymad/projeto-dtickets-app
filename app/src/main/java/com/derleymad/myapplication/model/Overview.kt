package com.derleymad.myapplication.model

data class Overview(
    val type :  String,
    val my_name : String,
    val abertos : String,
    val atribuidos : String,
    val atrasados : String,
    val fechados : String,
    val reabertos : String,
    val tempo_de_servico : String,
    val tempo_de_resposta : String
)
