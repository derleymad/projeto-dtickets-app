package com.derleymad.myapplication.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.derleymad.myapplication.R
import com.derleymad.myapplication.SearchActivity
import com.derleymad.myapplication.TicketActivity
import com.derleymad.myapplication.model.Ticket
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.concurrent.Executors

class GetTicketByNumberRequest(private val callback: SearchActivity){

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var responseCode : String

    val url =
        "https://atendimento.ufca.edu.br/scp/login.php"

    interface Callback{
        fun onPreExecuteQuery()
        fun onResultQuery(tickets: List<Ticket>)
        fun onFailureQuery(message: String)
    }

    fun execute(username:String, password:String,number:String){

        val urlQuery=
            "https://atendimento.ufca.edu.br/scp/tickets.php?a=search&query=$number&basic_search=Pesquisar"

        callback.onPreExecuteQuery()
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

                val page= Jsoup
                    .connect(urlQuery)
                    .cookies(loginForm.cookies())
                    .get()

                val table : Elements = page.select("table")[1].select("tbody").select("tr")

                val id = table.select("td:nth-child(1)").select("input").eachAttr("value")
                val number = table.select("td:nth-child(2)").select("a").eachText()
                val email = table.select("td:nth-child(2)").eachAttr("title")
                val data = table.select("td:nth-child(3)").eachText()
                val assunto = table.select("td:nth-child(4)").select("a").eachText()
                val size = table.select("td:nth-child(4)").select("small").eachText()
                val de = table.select("td:nth-child(5)").eachText()
                val prioridade = table.select("td:nth-child(6)").eachText()
                val para = table.select("td:nth-child(7)").eachText()
//                Log.i("idTESTE",id.toString())

                for ( i in 0 until id.size){
                    tickets.add(
                        Ticket(
                            id=id[i],
                            numero=number[i],
                            email=email[i],
                            data=data[i],
                            assunto = assunto[i] ,
                            de = de[i],
                            prioridade = prioridade[i],
                            para = para[i],
                            type = "aberto",
                            size = size[i]
                        )
                    )
                }

                handler.post{callback.onResultQuery(tickets)}

            }catch (e: IOException){
                val message = e.message ?: callback.getString(R.string.uknown_error)
                handler.post { callback.onFailureQuery(message)  }
            }
        }
        //FIM DO EXECUTORS
    }

}