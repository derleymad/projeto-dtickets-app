package com.derleymad.myapplication.ui.fragments.pager

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.App
import com.derleymad.myapplication.TicketActivity
import com.derleymad.myapplication.adapter.TicketsAdapter
import com.derleymad.myapplication.databinding.FragmentMeusBinding
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.utils.GetTicketsMeusRequest
import com.derleymad.myapplication.utils.Pojo
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executors


class MeusFragment : Fragment(), GetTicketsMeusRequest.Callback {

    private lateinit var binding : FragmentMeusBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMeusBinding.inflate(layoutInflater)
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
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            GetTicketsMeusRequest(this@MeusFragment).execute(username, password)
        }
            GetTicketsMeusRequest(this@MeusFragment).execute(username,password)
//        bindPojo()
    }
    fun bindPojo(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.rvMeus.adapter  = TicketsAdapter(Pojo().getTickets()){
            it -> val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra("id",it)
            startActivity(intent)
        }
        binding.rvMeus.layoutManager = LinearLayoutManager(context)
        binding.rvMeus.visibility = View.VISIBLE
    }

    override fun onPreExecute() {
    if(binding.swipeRefresh.isRefreshing){
        binding.included.errorContainer.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onResult(tickets: List<Ticket>) {
        binding.rvMeus.adapter = TicketsAdapter(tickets) { it -> val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        }
        binding.rvMeus.layoutManager = LinearLayoutManager(view?.context ?: null)
        binding.progressBar.visibility = View.INVISIBLE
        binding.rvMeus.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onFailure(message: String) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.swipeRefresh.isRefreshing = false
        binding.included.errorContainer.visibility = View.VISIBLE
        Snackbar.make(binding.root,"Sem conexão",Snackbar.LENGTH_SHORT)
            .show()
        Log.e("erro_internet_meus",message)
    }
    private fun checkNetwork() : Boolean{
        val connectivityManager = view?.context?.getSystemService(Context.CONNECTIVITY_SERVICE)
             as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && (networkInfo.isAvailable || networkInfo.isConnected)
    }
}