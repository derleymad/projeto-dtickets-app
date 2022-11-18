package com.derleymad.myapplication.ui.fragments.pager.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.App
import com.derleymad.myapplication.TicketActivity
import com.derleymad.myapplication.adapter.TicketsAdapter
import com.derleymad.myapplication.databinding.FragmentFixadosBinding
import com.derleymad.myapplication.model.Ticket
import java.util.concurrent.Executors


class FixadosFragment : Fragment() {

    private lateinit var binding :FragmentFixadosBinding
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var list : MutableList<Ticket>
    private var newChanges = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFixadosBinding.inflate(layoutInflater)
        getDB()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            getDB()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        if(newChanges){
            binding.swipeRefresh.isRefreshing = true
            getDB()
            newChanges = false
        }
        super.onResume()
    }

    fun getDB(){
        executor.execute {
            val app = (activity?.application as App)
            val dao = app.db.TicketDao()
            val response = dao.getAllRegister()

            list = mutableListOf()

            if (response.isEmpty()){
                handler.post {
                    binding.errorDbContainer.visibility = View.VISIBLE
                    binding.rvFixados.visibility = View.INVISIBLE
                    binding.swipeRefresh.isRefreshing = false
                }

            }else{
                binding.errorDbContainer.visibility = View.INVISIBLE
                binding.swipeRefresh.isRefreshing = false
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
                }
                handler.post{
                    binding.swipeRefresh.isRefreshing = false
                    binding.rvFixados.visibility = View.VISIBLE
                    binding.rvFixados.adapter = TicketsAdapter(list){ it ->
                        val intent = Intent(context, TicketActivity::class.java)
                        intent.putExtra("id", it)
                        startActivity(intent)
                    }
                    val layout = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,true)
                    binding.rvFixados.layoutManager = layout
                    layout.stackFromEnd = true
                    binding.rvFixados.scrollToPosition(list.size-1)
                }
            }
        }
    }
}
