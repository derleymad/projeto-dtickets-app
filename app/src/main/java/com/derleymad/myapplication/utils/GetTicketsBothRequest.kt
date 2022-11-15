package com.derleymad.myapplication.utils

import android.os.Handler
import android.os.Looper
import com.derleymad.myapplication.R
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.ui.fragments.pager.BothFragment
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.concurrent.Executors

class GetTicketsBothRequest(private val callback: BothFragment){

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    val url =
        "https://atendimento.ufca.edu.br/scp/login.php"
    val urlAbertos =
        "https://atendimento.ufca.edu.br/scp/tickets.php?sort=date&order=DESC&"
    val urlRespondidos =
        "https://atendimento.ufca.edu.br/scp/tickets.php?sort=date&order=DESC&status=answered"

    val urlBoth = mutableListOf(urlAbertos,urlRespondidos)

    interface Callback{
        fun onPreExecute()
        fun onResult(tickets:List<Ticket>)
        fun onFailure(message: String)
    }

    fun execute(username:String, password:String){
        callback.onPreExecute()
        executor.execute{
            val tickets = mutableListOf<Ticket>()
            try{
                val loginForm =
                    Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .ignoreHttpErrors(true)
                        .execute()

                val doc: Document = Jsoup.connect(url)
                    .data("userid", "$username")
                    .data("passwd", "$password")
                    .cookies(loginForm.cookies())
                    .post()

                for(i in urlBoth){
                    val page = Jsoup
                        .connect(i)
                        .cookies(loginForm.cookies())
                        .get()

                    val table : Elements = page.select("table")[1].select("tbody").select("tr")
//                    val type = page.select(".active")[1].text()
                    val id = table.select("td:nth-child(1)").select("input").eachAttr("value")
                    val number = table.select("td:nth-child(2)").select("a").eachText()
                    val email = table.select("td:nth-child(2)").eachAttr("title")
                    val data = table.select("td:nth-child(3)").eachText()
                    val assunto = table.select("td:nth-child(4)").select("a").eachText()
                    val size = table.select("td:nth-child(4)").select("small").eachText()
                    val de = table.select("td:nth-child(5)").eachText()
                    val prioridade = table.select("td:nth-child(6)").eachText()
                    val para = table.select("td:nth-child(7)").eachText()

                    for ( j in 0 until id.size){
                        tickets.add(
                            Ticket(
                                id=id[j],
                                numero=number[j],
                                email=email[j],
                                data=data[j],
                                assunto = assunto[j] ,
                                de = de[j],
                                prioridade = prioridade[j],
                                para = para[j],
                                size = size[j],
                                type = if(i.contains("answered")) "respondido" else "aberto"),
                        )
                    }
                }
                handler.post{callback.onResult(tickets)}
            }catch (e: IOException){
                val message = e.message ?: callback.getString(R.string.uknown_error)
                handler.post { callback.onFailure(message)  }
            }
        }
        //FIM DO EXECUTORS
    }

}