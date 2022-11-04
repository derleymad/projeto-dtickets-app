package com.derleymad.myapplication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.derleymad.myapplication.R
import com.derleymad.myapplication.TicketActivity
import com.derleymad.myapplication.adapter.TicketsAdapter
import com.derleymad.myapplication.databinding.FragmentMeusBinding
import com.derleymad.myapplication.databinding.FragmentRespondidosBinding
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.utils.GetTicketsAbertosRequest
import com.derleymad.myapplication.utils.GetTicketsFechadosRequest
import com.derleymad.myapplication.utils.GetTicketsMeusRequest
import com.derleymad.myapplication.utils.GetTicketsRespondidosRequest


class RespondidosFragment : Fragment(), GetTicketsRespondidosRequest.Callback{

    private lateinit var binding : FragmentRespondidosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRespondidosBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreference =  view.context.getSharedPreferences("credentials", Context.MODE_PRIVATE)

        val username = sharedPreference.getString("username","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        val password = sharedPreference.getString("password","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        GetTicketsRespondidosRequest(this@RespondidosFragment).execute(username,password)
    }

    override fun onPreExecute() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onResult(tickets: List<Ticket>) {
        binding.rvRespondidos.adapter = TicketsAdapter(tickets) { it -> val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        }
        binding.rvRespondidos.layoutManager = LinearLayoutManager(view?.context ?: null)
        binding.progressBar.visibility = View.INVISIBLE
        binding.rvRespondidos.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {

    }


}