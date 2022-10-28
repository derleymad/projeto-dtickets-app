package com.derleymad.myapplication.model

data class TicketDetail(
    val nome : String,
    val setor : String,
    val email : String,
    val ticketNumber : String,
    val descricao : String,
    val campus : String,
    val numero : String,
    val firstMessage : String,
    val prioridade : String,
    val status : String,
    val blocoSala : String,
    val message : List<Mensagens>

)
