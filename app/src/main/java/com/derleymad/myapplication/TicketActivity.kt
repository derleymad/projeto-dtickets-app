package com.derleymad.myapplication

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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.adapter.TicketMensagemAdapter
import com.derleymad.myapplication.databinding.ActivityTicketBinding
import com.derleymad.myapplication.model.FavTicket
import com.derleymad.myapplication.model.Mensagem
import com.derleymad.myapplication.model.TicketDetail
import com.derleymad.myapplication.utils.GetTicketDetailsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.*

class TicketActivity : AppCompatActivity(), GetTicketDetailsRequest.Callback{
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var binding : ActivityTicketBinding
    private lateinit var username : String
    private lateinit var password: String
    private lateinit var numero : String
    private lateinit var ticketDetail : TicketDetail
    private var isready = false
    private var isfixed = false
    private var msgs = mutableListOf<Mensagem>()

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


        binding.back.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            if(binding.editMessage.text.isNotEmpty()){
                binding.btnSend.visibility = View.GONE
                binding.editMessage.setTextColor(resources.getColor(R.color.gray_text))
                loginAndPostMessage(binding.editMessage.text.toString(),id)

            }
        }

        binding.btnScrollDown.setOnClickListener {
            binding.rvMensagens.smoothScrollToPosition(msgs.size-1)
        }

        binding.btnMore.setOnClickListener {
            val popupMenu = PopupMenu(this,binding.btnMore)
            popupMenu.menuInflater.inflate(R.menu.menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_atribuir_para_mim ->
                        Toast.makeText(
                            this@TicketActivity,
                            "You Clicked : " + item.title,
                            Toast.LENGTH_SHORT
                        ).show()
                    R.id.atribuir_para_alguem ->
                        Toast.makeText(
                            this@TicketActivity,
                            "You Clicked : " + item.title,
                            Toast.LENGTH_SHORT
                        ).show()
                    R.id.fechar_ticket ->
                        openDialogAndCallToClose(numero,id)
                }
                true
            }
            popupMenu.show()
        }

        GetTicketDetailsRequest(this@TicketActivity).execute(username,password,id)
//        bindPojo(Pojo().getTicketDetail())

    }
    private fun bindPojo(ticket:TicketDetail){
        populateView(ticket)
    }

    private fun loginAndPostMessage(message: String,id:String) {
        val url = "https://atendimento.ufca.edu.br/scp/tickets.php?id=$id"
        binding.webView.clearCache(true)
        binding.webView.loadUrl(url)
        binding.webView.settings.javaScriptEnabled = true
        var firstTime = true


        binding.webView.webViewClient = object  : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {

                binding.webView.loadUrl("javascript:{" +
                        "document.getElementById('name').value = '$username';"+
                        "document.getElementById('pass').value = '$password';"+
                        "document.getElementsByClassName('submit')[0].click();" +
                        "};" )

                binding.webView.loadUrl("javascript:{" +
                        "document.getElementById('response').value = '$message';"+
                        "document.getElementsByClassName('btn_sm')[0].click();"+
                        "};" )
                firstTime = false

            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if(!firstTime){
                    msgs.add(
                        Mensagem(
                            data="agora",
                            status="aberto",
                            de=ticketDetail.myName,
                            mensagem = "$message"
                        )
                    )
                    binding.webView.stopLoading()
                    binding.editMessage.setTextColor(resources.getColor(R.color.black))
                    binding.editMessage.text.clear()
                    binding.btnSend.visibility = View.VISIBLE
                    binding.rvMensagens.adapter?.notifyItemInserted(msgs.size-1)
                    binding.rvMensagens.scrollToPosition(msgs.size-1)
                }
                super.onPageStarted(view, url, favicon)
            }
        }
    }


    private fun getIsFixedFromDB(id:String){
        Thread{
            val app = (application as App)
            val dao = app.db.TicketDao()
            val response = dao.getRegisterById(id)

            if(response!=null){
                runOnUiThread{
                    binding.btnFlag.setImageDrawable(resources.getDrawable(R.drawable.ic_pin_fixed))
                    isfixed = true
                    binding.btnFlag.setOnClickListener {
                        openDialogAndRemoveIntoDB(response)
                    }
                }
            }else{
                isfixed = false
                binding.btnFlag.setOnClickListener {
                    val favTicket = FavTicket(
                        ticketDetail.id,
                        isfixed = true,
                        ticketDetail.myName,
                        ticketDetail.nome,
                        ticketDetail.para,
                        ticketDetail.descricao,
                        ticketDetail.status,
                        ticketDetail.prioridade,
                        ticketDetail.setor,
                        ticketDetail.dataCriacao,
                        ticketDetail.email,
                        ticketDetail.numeroTicket
                    )
                    openDialogAndSaveIntoDB(favTicket)
                }
            }
        }.start()
    }
    private fun openDialogAndRemoveIntoDB(ticket: FavTicket){

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.remove_request))
            .setMessage(getString(R.string.remove_request_description))
            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                val app = (application as App)
                val dao = app.db.TicketDao()
                Thread {
                    try {
                        dao.delete(ticket)
                        binding.btnFlag.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_push_pin_24))
                    } finally {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.remove_sucess),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .create()
            .show()
    }

    private fun openDialogAndCallToClose(numero:String, id:String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Fechar o ${numero.lowercase(Locale.ROOT)}")
        val input = EditText(this)
        input.hint = "Diga um motivo"
        input.setTextColor(resources.getColor(R.color.textGray))
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setMessage("Deseja Realmente remover o ticket?")

        builder.setPositiveButton("Fechar", DialogInterface.OnClickListener { dialog, which ->
            if(input.text.isNotEmpty()){
                loginAndCloseTicket(id,input.text.toString())

            }else{
                Snackbar.make(binding.root,"Por favor, informe um motivo!",Snackbar.LENGTH_SHORT).show()
            }
        })
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }

    private fun loginAndCloseTicket(id:String, message : String){
        binding.progressBar.visibility = View.VISIBLE
        val url = "https://atendimento.ufca.edu.br/scp/tickets.php?id=$id"
        binding.webView.clearCache(true)
        binding.webView.loadUrl(url)
        binding.webView.settings.javaScriptEnabled = true
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
                        "document.getElementById('ticket-close').click();"+
                        "document.getElementById('ticket_status_notes').value = '$message';"+
                        "document.querySelectorAll('input')[45].click();"+
                        "};" )
                firstTime = false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if(!firstTime){
                    binding.webView.stopLoading()
                    binding.progressBar.visibility = View.INVISIBLE
                    // REFRESH MAIN ACTIVITY
                    binding.rvMensagens.adapter?.notifyDataSetChanged()
                    val snack = Snackbar.make(binding.root,"Ticket fechado e resposta enviada!",Snackbar.LENGTH_SHORT)
                        .setActionTextColor(resources.getColor(R.color.black))
                        .setAction("Desfazer"){
                                Snackbar.make(binding.root, "Mudanças desfeitas", Snackbar.LENGTH_SHORT).show()
                        }
                        .setBackgroundTint(resources.getColor(R.color.blue_enabled))
                    snack.show()
                }
                super.onPageStarted(view, url, favicon)
            }
        }
    }

    private fun populateView(ticket:TicketDetail){
        this.msgs.addAll(ticket.msgs)
        this.numero = ticket.numeroTicket

        binding.apply {
            progressBar.visibility = View.INVISIBLE
            mainContainer.visibility = View.VISIBLE
            rvMensagens.adapter = TicketMensagemAdapter(msgs, ticket.myName)
            rvMensagens.layoutManager = LinearLayoutManager(this@TicketActivity)
            nome.text = ticket.nome
            ticketDescription.text = ticket.descricao
            campus.text = ticket.campus
            numero.text = ticket.numero
            number.text = ticket.numeroTicket
            status.text = ticket.status
            sala.text = "${ticket.bloco}-${ticket.sala}"
        }

    }
    private fun openDialogAndSaveIntoDB(ticket : FavTicket) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.save_request))
            .setMessage(getString(R.string.save_request_description))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val app = (application as App)
                val dao = app.db.TicketDao()
                Thread {
                    try {
                        dao.insert(ticket)
                        binding.btnFlag.setImageDrawable(resources.getDrawable(R.drawable.ic_pin_fixed))
                    } finally {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.save_sucess),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .create()
            .show()
    }


    override fun onPreExecute() {
        this.msgs.clear()
    }

    override fun onResult(ticket: TicketDetail) {
        isready = true
        ticketDetail = ticket
        populateView(ticket)
        getIsFixedFromDB(ticket.id)
    }

    override fun onFailure(message: String) {
        Log.e("error","errorTicketActivity onFailure to GetTicketDetailsRequest, $message")
    }


}
