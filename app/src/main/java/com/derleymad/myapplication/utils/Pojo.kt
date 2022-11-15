package com.derleymad.myapplication.utils

import com.derleymad.myapplication.model.Mensagem
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.model.TicketDetail

class Pojo {
    fun getTickets() : List<Ticket>{
        val list = mutableListOf<Ticket>()
        for(i in 0..10){
            list.add(
                Ticket(
                    id = "123456",
                    email = "xxx@xxx.xxx",
                    assunto = "xxx xxx xxx",
                    data = "xx/xx/xxxx",
                    para = "xxxxx",
                    prioridade = "xxxxxx",
                    de = "xxxxx",
                    numero = "xxxxxx",
                    type = "pojo",
                    size = "999"
                )
            )
        }
        return list
    }

    fun getTicketDetail() : TicketDetail{
        return TicketDetail(
            msgs = mutableListOf(Mensagem("xx/xx/xxxx","xxxxx","xxxxx","xxxxxxxxxxxx xxxxxxxxxxx x xxxxxxxx xxx")),
            myName = "xxxxx",
            descricao = "xxxxxxxxxxxxxxxxxxxxxxxx",
            status = "xxxxxxx",
            prioridade = "xxxxx",
            setor = "xxxx",
            dataCriacao = "xx/xx/xxxx",
            email = "xxx@xxx.xxx",
            numeroTicket = "xxxxxxx",
            numero = "xxxxxx",
            para = "xxxxx xxxxx",
            slaPlan = "xxxxxx",
            dueDate = "xxxxxx",
            ultimaMensagem = "xx/xx/xxxx xx:xx",
            ultimaResposta = "xx/xx/xxxx xx:xx",
            servicos = "xxxxxxxxx",
            campus = "xxxxxxxx",
            sala = "xxx",
            bloco = "x",
            setorSolicitante = "xxxxxxxxxxxxx",
            nome = "xxxxxx",
            id = "xxxxxx",
            type = "closed"
        )
    }
}