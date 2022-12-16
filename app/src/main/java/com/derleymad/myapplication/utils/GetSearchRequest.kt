package com.derleymad.myapplication.utils

import android.os.Handler
import android.os.Looper
import com.derleymad.myapplication.R
import com.derleymad.myapplication.SearchActivity
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.ui.fragments.pager.AbertosFragment
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.concurrent.Executors

class GetSearchRequest(private val callback: SearchActivity){

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var responseCode : String

    val url =
        "https://atendimento.ufca.edu.br/scp/login.php"

    interface Callback{
        fun onPreExecute()
        fun onResult(result : List<String>)
        fun onFailure(message: String)
    }

    fun execute(username:String, password:String,query:String){
        val urlSearch=
            "https://atendimento.ufca.edu.br/scp/ajax.php/tickets/lookup?q=$query"

        callback.onPreExecute()
        executor.execute{
//            val tickets = mutableListOf<Ticket>()

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
                    .connect(urlSearch)
                    .cookies(loginForm.cookies())
                    .get()




                val jsonRoot= JSONArray(page.body().wholeText())


                val elements = mutableListOf<String>()

                for(i in 0 until jsonRoot.length()){
                    elements.add(jsonRoot.getJSONObject(i).getString("value"))
                }
//                val jsonDataRoot = jsonRoot.getJSONArray("id")
//                val jsonData = jsonDataRoot.getJSONArray(0)


                handler.post{callback.onResult(elements)}

            }catch (e: IOException){
                val message = e.message ?: callback.getString(R.string.uknown_error)
                handler.post { callback.onFailure(message)  }
            }
        }
        //FIM DO EXECUTORS
    }

}