package com.derleymad.myapplication.utils

import android.accounts.NetworkErrorException
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.derleymad.myapplication.MainActivity
import com.derleymad.myapplication.R
import com.derleymad.myapplication.databinding.FragmentAbertosBinding
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.ui.AbertosFragment
import com.derleymad.myapplication.ui.RespondidosFragment
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.concurrent.Executors

class GetTicketsRespondidosRequest(private val callback: RespondidosFragment){

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    val url =
        "https://atendimento.ufca.edu.br/scp/login.php"
    val urlRespondidos =
        "https://atendimento.ufca.edu.br/scp/tickets.php?sort=date&order=DESC&status=answered"

    interface Callback{
        fun onPreExecute()
        fun onResult(tickets:List<Ticket>)
        fun onFailure(message: String)
    }

    fun execute(username:String, password:String){
        callback.onPreExecute()
        executor.execute{
            try{
                val ticketsAbertos = getTicketsAbertos(username,password)
                handler.post{callback.onResult(ticketsAbertos)}

            }catch (e: NetworkErrorException){
                val message = e.message ?: callback.getString(R.string.uknown_error)
                Log.e("Teste",message,e)
                handler.post { callback.onFailure(message)  }
            }finally {
            }

        }
        //FIM DO EXECUTORS
    }

    fun getTicketsAbertos(username:String, password:String) : List<Ticket>{
        val tickets = mutableListOf<Ticket>()

        val loginForm =
            Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute()

        val doc: Document = Jsoup.connect(url)
            .data("userid", "$username")
            .data("passwd", "$password")
            .cookies(loginForm.cookies())
            .post()

        val page= Jsoup
            .connect(urlRespondidos)
            .cookies(loginForm.cookies())
            .get()

        val table : Elements = page.select("table")[1].select("tbody").select("tr")

        val id = table.select("td:nth-child(1)").select("input").eachAttr("value")
        val number = table.select("td:nth-child(2)").select("a").eachText()
        val email = table.select("td:nth-child(2)").eachAttr("title")
        val data = table.select("td:nth-child(3)").eachText()
        val assunto = table.select("td:nth-child(4)").select("a").eachText()
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
                    para = para[i]
                )
            )
        }
        return tickets

    }
}