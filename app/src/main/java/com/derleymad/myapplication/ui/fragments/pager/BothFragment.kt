package com.derleymad.myapplication.ui.fragments.pager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.TicketActivity
import com.derleymad.myapplication.adapter.TicketsAdapter
import com.derleymad.myapplication.databinding.FragmentBothBinding
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.utils.GetTicketsBothRequest
import com.derleymad.myapplication.utils.GetTicketsMeusRequest
import com.google.android.material.snackbar.Snackbar

class BothFragment : Fragment() , GetTicketsBothRequest.Callback{

    private lateinit var binding : FragmentBothBinding
    private var tickets = mutableListOf<Ticket>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentBothBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreference =  view.context.getSharedPreferences("credentials", Context.MODE_PRIVATE)

        val username = sharedPreference.getString("username","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        val password = sharedPreference.getString("password","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        GetTicketsBothRequest(this@BothFragment).execute(username,password)

        binding.swipeRefresh.setOnRefreshListener {
            binding.rvBoth.visibility = View.INVISIBLE
            binding.swipeRefresh.isRefreshing = true
            GetTicketsBothRequest(this@BothFragment).execute(username,password)
        }

        binding.included.retry.setOnClickListener {
            binding.swipeRefresh.isRefreshing = true
            binding.rvBoth.visibility = View.INVISIBLE
            GetTicketsBothRequest(this@BothFragment).execute(username, password)
        }
        GetTicketsBothRequest(this@BothFragment).execute(username,password)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPreExecute() {
        binding.included.errorContainer.visibility = View.INVISIBLE
        if(binding.swipeRefresh.isRefreshing){
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onResult(tickets: List<Ticket>) {
        this.tickets.addAll(tickets)
        binding.rvBoth.visibility = View.VISIBLE
        binding.rvBoth.adapter = TicketsAdapter(tickets) { it -> val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        }
        binding.rvBoth.layoutManager = LinearLayoutManager(view?.context ?: null)
        binding.progressBar.visibility = View.INVISIBLE
        binding.rvBoth.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onFailure(message: String) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.included.errorContainer.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = false
        Snackbar.make(binding.root,"Sem conexão", Snackbar.LENGTH_SHORT).show()
        Log.e("error_internet",message)
    }

    override fun onDestroyView() {
        Log.i("saveTeste","salvando no db")
        super.onDestroyView()
    }

    override fun onStop() {
        Log.i("saveTeste","salvando no db")
        super.onStop()
    }

}