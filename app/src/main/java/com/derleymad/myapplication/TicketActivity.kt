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
import android.view.animation.AlphaAnimation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.adapter.TicketMensagemAdapter
import com.derleymad.myapplication.databinding.ActivityTicketBinding
import com.derleymad.myapplication.model.FavTicket
import com.derleymad.myapplication.model.Mensagem
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.model.TicketDetail
import com.derleymad.myapplication.utils.GetTicketByNumberRequest
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
    private var isFixed = false
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

       val isInteract = getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("isInteract",false)

       val id = intent?.extras?.getString("id", "110652") ?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )

        if (!isInteract){
            binding.editSendContainer.visibility = View.GONE
        }else{
            setClickOnMoreBtn(id)
            binding.editSendContainer.visibility = View.VISIBLE
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            if(binding.editMessage.text.isNotEmpty()){
                binding.btnSend.visibility = View.GONE
                binding.editMessage.setTextColor(ContextCompat.getColor(this@TicketActivity,R.color.gray_text))
                loginAndPostMessage(binding.editMessage.text.toString(),id)
            }
        }

        binding.btnScrollDown.setOnClickListener {
            binding.rvMensagens.smoothScrollToPosition(msgs.size-1)
        }

        GetTicketDetailsRequest(this@TicketActivity).execute(username,password,id)
//        bindPojo(Pojo().getTicketDetail())

    }
    private fun bindPojo(ticket:TicketDetail){
        populateView(ticket)
    }

    private fun setClickOnMoreBtn(id: String){
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

    private fun checkSeEstaFixado(id:String) : Boolean{
        Thread{
            val app = (application as App)
            val dao = app.db.TicketDao()
            val response = dao.getRegisterById(id)

            if(response!=null){
                binding.btnFlag.setImageDrawable(ContextCompat.getDrawable(this@TicketActivity,R.drawable.ic_pin_fixed))
                isFixed = true
            }else{
                binding.btnFlag.setImageDrawable(ContextCompat.getDrawable(this@TicketActivity,R.drawable.ic_baseline_push_pin_24))
                isFixed = false
            }
        }.start()
        return isFixed
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

    override fun onPreExecute() {
        this.msgs.clear()
    }

    override fun onResult(ticket: TicketDetail) {
        checkSeEstaFixado(ticket.id)

        binding.btnFlag.setOnClickListener {
            it.startAnimation(AlphaAnimation(1F,0.8F))
            if(isFixed){
                isFixed = false
                removeTicketFromDB(ticket.id)
                Snackbar.make(binding.root,"Ticket removido!",Snackbar.LENGTH_SHORT).show()
                binding.btnFlag.setImageDrawable(ContextCompat.getDrawable(this@TicketActivity,R.drawable.ic_baseline_push_pin_24))
            }else{
                isFixed = true
                binding.btnFlag.setImageDrawable(ContextCompat.getDrawable(this@TicketActivity,R.drawable.ic_pin_fixed))
                Snackbar.make(binding.root,"Ticket fixado!",Snackbar.LENGTH_SHORT).show()
                insertTicketIntoDB()
            }
        }
//        getIsFixedFromDB(ticket.id)
        ticketDetail = ticket
        populateView(ticket)
    }

    fun removeTicketFromDB(ticketID : String){
        val app = (application as App)
        val dao = app.db.TicketDao()

        Thread{
            val response = dao.getRegisterById(ticketID)
            dao.delete(response)
        }.start()
    }

    fun insertTicketIntoDB(){
        val app = (application as App)
        val dao = app.db.TicketDao()
        Thread{
            val favTicket = FavTicket(
                id=ticketDetail.id,
                type = ticketDetail.type,
                isfixed = true,
                myName = ticketDetail.myName,
                de = ticketDetail.nome,
                para = ticketDetail.para,
                descricao = ticketDetail.descricao,
                status = ticketDetail.status,
                prioridade = ticketDetail.prioridade,
                setor = ticketDetail.setor,
                dataCriacao = ticketDetail.dataCriacao,
                email = ticketDetail.email,
                numeroTicket = ticketDetail.numeroTicket
            )
            try{
                dao.insert(favTicket)
            }finally {
            }
        }.start()
    }

    override fun onFailure(message: String) {
        Log.e("messageerror",message)
        Snackbar.make(binding.root,"Sem conexão",Snackbar.LENGTH_SHORT).show()
    }


}
