package com.derleymad.myapplication

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.adapter.TicketMensagemAdapter
import com.derleymad.myapplication.databinding.ActivityTicketBinding
import com.derleymad.myapplication.model.Mensagem
import com.derleymad.myapplication.model.TicketDetail
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.*

class TicketActivity : AppCompatActivity() {
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var binding : ActivityTicketBinding
    private lateinit var ticketDetail : TicketDetail
    private lateinit var username : String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference =  getSharedPreferences("credentials", Context.MODE_PRIVATE)
        editor = sharedPreference.edit()

        username = sharedPreference.getString("username","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        password = sharedPreference.getString("password","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )

        val id = intent?.extras?.getString("id", "110652") ?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        loginAndGetTicket(username,password,id)


        binding.btnCloseTicket.setOnClickListener {
            if(ticketDetail.status=="Closed"){
                Log.i("fechado","ticket fechado ja")
            }else{
                openDialogAndCloseTicket(ticketDetail)
                Log.i("fechando","fechando ticket")
            }
        }
    }

    fun openDialogAndCloseTicket(ticket: TicketDetail){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Fechar o ${ticket.numeroTicket.lowercase(Locale.ROOT)}")
        val input = EditText(this)
        input.hint = "Diga um motivo (ou não)"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setMessage("Deseja Realmnete remover o ticket?")

        builder.setPositiveButton("Fechar", DialogInterface.OnClickListener { dialog, which ->
            if(input.text.isNotEmpty()){
                loginAndCloseTicket(ticket.id,input.text.toString())
            }
        })
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }

    fun loginAndGetTicket(username: String,password: String, id: String) {

        val url = "https://atendimento.ufca.edu.br/scp/tickets.php?id=$id"
        try {
            Thread{
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
                    .connect(url)
                    .cookies(loginForm.cookies())
                    .get()

                val content : Elements = page.select("#content")

                val numeroTicket = content
                    .select("table")[0]
                    .select("tbody")
                    .select("tr").select("td")[0].select("h2").select("a").text()

                val firstTableLeft = content
                    .select("table")[1].select("tbody").select("tr").select("td:nth-child(1)")

                val firstTableRight = content
                    .select("table")[1].select("tbody").select("tr").select("td:nth-child(2)")

                val secondTable = content
                    .select(".ticket_info")[1]

                val secondTableLeft= secondTable.select("tbody").select("tr").select("td:nth-child(1)")
                val secondTableRight = secondTable.select("tbody").select("tr").select("td:nth-child(2)")

                val thirdTable = content.select(".ticket_info")[2]

                val descricao = content.select(">h2").text()
                val id = content.select("table").select("tbody").select("tr").select("td").select("h2").select("a").attr("href")
                val status = firstTableLeft.select("table").select("tbody").select("tr")[0].select("td").text()
                val prioridade = firstTableLeft.select("table").select("tbody").select("tr")[1].select("td").text()
                val setor = firstTableLeft.select("table").select("tbody").select("tr")[2].select("td").text()
                val dataCriacao = firstTableLeft.select("table").select("tbody").select("tr")[3].select("td").text()
                val email = firstTableRight.select("table").select("tbody").select("tr")[1].select("td").select("span").text()
                val numero = firstTableRight.select("table").select("tbody").select("tr")[2].select("td").select("span").text()
                val para = secondTableLeft.select("table").select("tbody").select("tr")[0].select("td").text()
                val slaPlan = secondTableLeft.select("table").select("tbody").select("tr")[1].select("td").text()
                val dueDate = secondTableLeft.select("table").select("tbody").select("tr")[2].select("td").text()
                val ultimaMensagem = secondTableRight.select("table").select("tbody").select("tr")[1].select("td").text()
                val ultimaResposta = secondTableRight.select("table").select("tbody").select("tr")[2].select("td").text()
                val servicos = thirdTable.select("tbody").select("tr").select("td").select("table").select("tbody").select("tr")[0].select("td").text()
                val campus = thirdTable.select("tbody").select("tr").select("td").select("table").select("tbody").select("tr")[1].select("td").text()
                val sala = thirdTable.select("tbody").select("tr").select("td").select("table").select("tbody").select("tr")[2].select("td").text()
                val bloco = thirdTable.select("tbody").select("tr").select("td").select("table").select("tbody").select("tr")[3].select("td").text()
                val setorSolicitante = thirdTable.select("tbody").select("tr").select("td").select("table").select("tbody").select("tr")[4].select("td").text()
                val nome = thirdTable.select("tbody").select("tr").select("td").select("table").select("tbody").select("tr")[5].select("td").text()

                val qtdTickets = content.select("#threads").select("li").select("a").text()
                val tableMsg : Elements = content.select("#ticket_thread").select("table")
                val msgs = mutableListOf<Mensagem>()

                for (i in tableMsg){
                    val data = i.select("tbody").select("tr")[0].select("th").select("div").select("span")[0].wholeText()
                    val status = i.select("tbody").select("tr")[0].select("th").select("div").select("span")[1].wholeText()
                    val de = i.select("tbody").select("tr")[0].select("th").select("div").select("span")[2].select(".tmeta.faded.title").text()
                    val mensagem = i.select("tbody").select("tr")[1].select("td").select("div").text()

                    msgs.add(Mensagem(
                        data = data,
                        status = status,
                        de = de,
                        mensagem = mensagem))
                }

                //FIM DO FOR

                runOnUiThread {

                    Log.i("mensagens",msgs.toString())
                     ticketDetail = TicketDetail(
                         id = id.substring(15..20),
                         descricao  = descricao,
                         status = status,
                         prioridade = prioridade,
                         setor = setor,
                         dataCriacao = dataCriacao,
                         email = email,
                         numeroTicket = numeroTicket,
                         numero = numero,
                         para = para,
                         slaPlan = slaPlan,
                         dueDate = dueDate,
                         ultimaMensagem  = ultimaMensagem,
                         ultimaResposta = ultimaResposta,
                         servicos = servicos,
                         campus = campus,
                         sala = sala,
                         bloco = bloco,
                         setorSolicitante = setorSolicitante,
                         nome = nome
                    )


                    if(status == "Closed"){
                        binding.btnCloseTicket.text = "Reabrir"
                    }

                    binding.progressBar.visibility = View.INVISIBLE
                    binding.mainContainer.visibility = View.VISIBLE
                    binding.rvMensagens.adapter = TicketMensagemAdapter(msgs)
                    binding.rvMensagens.layoutManager = LinearLayoutManager(this@TicketActivity)
                    binding.nome.text = ticketDetail.descricao
                    binding.setor.text = ticketDetail.setorSolicitante
                    binding.email.text = ticketDetail.email
                    binding.campus.text = ticketDetail.campus
                    binding.numero.text = ticketDetail.numero
                    binding.number.text = ticketDetail.numeroTicket
                    binding.status.text = ticketDetail.status
                    binding.prioridade.text = prioridade
                    binding.sala.text = "$bloco-$sala"
                }

            }.start()
        }catch (e : NetworkErrorException){
            println(e.message)
        }
    }

    fun loginAndCloseTicket(id:String, message : String){
        binding.btnProgress.visibility = View.VISIBLE
        binding.btnCloseTicket.visibility = View.INVISIBLE
        val url = "https://atendimento.ufca.edu.br/scp/tickets.php?id=$id"
        binding.webView.clearCache(true)
        binding.webView.loadUrl(url)
        binding.webView.settings.setJavaScriptEnabled(true)
        var firstTime = true
        binding.webView.loadUrl("javascript:{" +
                "ins=document.getElementsByTagName('input');" +
                "ins[2].value='$username';" +
                "ins[3].value='$password';" +
                "ins[4].click();" +
                "};" )
        binding.webView.webViewClient = object  : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                binding.webView.loadUrl("javascript:{" +
                        "document.getElementById('reply_ticket_status').click();"+
                        "document.getElementById('response').value = '$message';"+
                        "document.getElementsByClassName('btn_sm')[0].click();"+
                        "};" )
                firstTime = false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if(!firstTime){
                    binding.webView.stopLoading()
                    binding.btnProgress.visibility = View.INVISIBLE
                    binding.btnCloseTicket.text = "Reabrir"
                    binding.btnCloseTicket.visibility = View.VISIBLE
                    val snack = Snackbar.make(binding.root,"Ticket fechado e resposta enviada!",Snackbar.LENGTH_SHORT)
                        .setActionTextColor(resources.getColor(R.color.black))
                        .setAction("Desfazer"){
                                Snackbar.make(binding.root, "Mudanças desfeitas", Snackbar.LENGTH_SHORT).show();
                            }
                        .setBackgroundTint(resources.getColor(R.color.green_ufca))
                    snack.show()
                }
                super.onPageStarted(view, url, favicon)
            }

        }

    }

}
