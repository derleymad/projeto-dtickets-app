package com.derleymad.myapplication.model

data class TicketDetail(
    val id : String,
    val myName : String,
    val descricao : String,
    val status : String,
    val prioridade : String,
    val setor : String,
    val dataCriacao : String,
    val email : String,
    val numeroTicket : String,
    val numero : String,
    val para : String,
    val slaPlan : String,
    val dueDate : String,
    val ultimaMensagem : String,
    val ultimaResposta : String,
    val servicos : String,
    val campus : String,
    val sala : String,
    val bloco : String,
    val setorSolicitante: String,
    val nome : String,
    val msgs : List<Mensagem>

)
