package com.derleymad.myapplication.utils

import android.os.Handler
import android.os.Looper
import com.derleymad.myapplication.R
import com.derleymad.myapplication.model.Overview
import com.derleymad.myapplication.ui.fragments.pager.dashboard.OverviewFragment
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.concurrent.Executors

class GetOverviewRequest(private val callback: OverviewFragment){

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var responseCode : String

    val url =
        "https://atendimento.ufca.edu.br/scp/login.php"
    val urlOverview =
        "https://atendimento.ufca.edu.br/scp/ajax.php/report/overview/table?group=staff&start=01/01/2000&stop=now"
    val urlOverviewDepartament =
        "https://atendimento.ufca.edu.br/scp/ajax.php/report/overview/table?group=dept&start=01/01/2000&stop=now"

    interface Callback{
        fun onPreExecute()
        fun onResult(overview:List<Overview>)
        fun onFailure(message: String)
    }

    fun execute(username:String, password:String){
        callback.onPreExecute()
        executor.execute{
            try{
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
                    .connect(urlOverview)
                    .cookies(loginForm.cookies())
                    .get()

                val pageDepartment = Jsoup
                    .connect(urlOverviewDepartament)
                    .cookies(loginForm.cookies())
                    .get()

                val jsonRootDepartamento = JSONObject(pageDepartment.body().wholeText())
                val jsonDataRootDepartamento = jsonRootDepartamento.getJSONArray("data")
                val jsonDataDepartamento = jsonDataRootDepartamento.getJSONArray(0)

                val overViewDepartamento : Overview = Overview(
                    type = "departamento",
                    my_name = jsonDataDepartamento.getString(0),
                    jsonDataDepartamento.getString(1),
                    jsonDataDepartamento.getString(2),
                    jsonDataDepartamento.getString(3),
                    jsonDataDepartamento.getString(4),
                    jsonDataDepartamento.getString(5),
                    jsonDataDepartamento.getString(6),
                    jsonDataDepartamento.getString(7)
                )

                val jsonRoot = JSONObject(page.body().wholeText())
                val jsonDataRoot = jsonRoot.getJSONArray("data")
                val jsonData = jsonDataRoot.getJSONArray(0)

                var overview : Overview = Overview(
                    type = "pessoal",
                    my_name = jsonData.getString(0),
                    jsonData.getString(1),
                    jsonData.getString(2),
                    jsonData.getString(3),
                    jsonData.getString(4),
                    jsonData.getString(5),
                    jsonData.getString(6),
                    jsonData.getString(7)
                )
                handler.post{callback.onResult(mutableListOf(overview,overViewDepartamento))}
            }catch (e: IOException){
                val message = e.message ?: callback.getString(R.string.uknown_error)
                handler.post { callback.onFailure(message)  }
            }finally {
            }
        }
        //FIM DO EXECUTORS
    }

}