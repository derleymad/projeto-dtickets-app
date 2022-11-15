package com.derleymad.myapplication.ui.fragments.pager.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.App
import com.derleymad.myapplication.R
import com.derleymad.myapplication.TicketActivity
import com.derleymad.myapplication.adapter.TicketsAdapter
import com.derleymad.myapplication.databinding.FragmentAbertosBinding
import com.derleymad.myapplication.databinding.FragmentFechadosBinding
import com.derleymad.myapplication.databinding.FragmentFixadosBinding
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.model.TicketDetail
import java.util.concurrent.Executors


class FixadosFragment : Fragment() {

    private lateinit var binding :FragmentFixadosBinding
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var list : MutableList<Ticket>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        executor.execute {
            val app = (activity?.application as App)
            val dao = app.db.TicketDao()
            val response = dao.getAllRegister()

            list = mutableListOf()

            if (response.isEmpty()){
                handler.post {
                    binding.errorDbContainer.visibility = View.VISIBLE
                }

            }else{
                for (i in response){
                    list.add(Ticket(
                        isfixed = true,
                        id = i.id,
                        email = i.email,
                        numero = i.numeroTicket,
                        data = i.dataCriacao,
                        de = i.de,
                        prioridade = i.prioridade,
                        type = i.type,
                        assunto = i.descricao,
                        para = i.para,
                        size = ""
                    ))
//                        i.descricao,i.dataCriacao,i.de,i.para,i.prioridade,type = i.type))
                }
                handler.post{
                    binding.rvFixados.adapter = TicketsAdapter(list){ it ->
                        val intent = Intent(context, TicketActivity::class.java)
                        intent.putExtra("id", it)
                        startActivity(intent)
                    }
                    binding.rvFixados.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }

        binding = FragmentFixadosBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("viewteste","viewcreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Log.i("viewteste","start")
        super.onStart()
    }

    override fun onResume() {
        Log.i("viewteste","resume")
        super.onResume()
    }
}
