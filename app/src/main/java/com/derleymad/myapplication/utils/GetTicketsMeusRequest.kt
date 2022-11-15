package com.derleymad.myapplication.utils

import android.os.Handler
import android.os.Looper
import com.derleymad.myapplication.R
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.ui.fragments.pager.MeusFragment
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.concurrent.Executors

class GetTicketsMeusRequest(private val callback: MeusFragment) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var responseCode: String
    val url =
        "https://atendimento.ufca.edu.br/scp/login.php"
    val urlMeus =
        "https://atendimento.ufca.edu.br/scp/tickets.php?sort=date&order=DESC&status=assigned"

    interface Callback {
        fun onPreExecute()
        fun onResult(tickets: List<Ticket>)
        fun onFailure(message: String)
    }

    fun execute(username: String, password: String) {
        callback.onPreExecute()
        executor.execute {
            val tickets = mutableListOf<Ticket>()

            try {
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
                    .connect(urlMeus)
                    .cookies(loginForm.cookies())
                    .get()

                val table : Elements = page.select("table")[1].select("tbody").select("tr")

                val id = table.select("td:nth-child(1)").select("input").eachAttr("value")
                val number = table.select("td:nth-child(2)").select("a").eachText()
                val email = table.select("td:nth-child(2)").eachAttr("title")
                val data = table.select("td:nth-child(3)").eachText()
                val assunto = table.select("td:nth-child(4)").select("a").eachText()
                val size = table.select("td:nth-child(4)").select("small").text()
                val de = table.select("td:nth-child(5)").eachText()
                val prioridade = table.select("td:nth-child(6)").eachText()
                val para = table.select("td:nth-child(7)").eachText()

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
                            type = "meus",
                            size = size
                        )
                    )
                }
                handler.post { callback.onResult(tickets) }
            } catch (e: IOException) {
                val message = e.message ?: callback.getString(R.string.uknown_error)
                handler.post { callback.onFailure(message) }
            }
        }
        //FIM DO EXECUTORS
    }
}

