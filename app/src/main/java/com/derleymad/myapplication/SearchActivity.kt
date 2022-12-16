package com.derleymad.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.adapter.TicketsAdapter
import com.derleymad.myapplication.databinding.ActivitySearchBinding
import com.derleymad.myapplication.model.Ticket
import com.derleymad.myapplication.utils.GetSearchRequest
import com.derleymad.myapplication.utils.GetTicketByNumberRequest

class SearchActivity : AppCompatActivity(), GetSearchRequest.Callback , GetTicketByNumberRequest.Callback{
    private lateinit var binding : ActivitySearchBinding
    private lateinit var username : String
    private lateinit var password : String
    private var list = mutableListOf<String>()
    var results = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreference =  getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()


        username = sharedPreference.getString("username","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        password = sharedPreference.getString("password","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )


        binding.searchview.requestFocus()
        binding.searchview.setOnCloseListener {
            list.clear()
            true
        }
        binding.searchview.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null && p0.isNotBlank() && p0.isNotEmpty()) {
                   GetSearchRequest(this@SearchActivity).execute(username,password,p0.toString())
                }else{
                    list.clear()
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
//                GetSearchRequest(this@SearchActivity).execute(username,password,p0.toString())
                return false
            }
        })
    }

    override fun onPreExecute() {
    }

    override fun onResult(result:List<String>) {
        list.clear()
        list.addAll(result)
        binding.listview.visibility = View.VISIBLE
        var listAdapter = ArrayAdapter(
            this@SearchActivity,R.layout.item_search,list
        )
        binding.listview.setOnItemClickListener { _, _, i, l ->
            GetTicketByNumberRequest(this@SearchActivity).execute(username, password,result[i])
            list.clear()
            listAdapter.notifyDataSetChanged()
//            binding.searchview.setQuery(result[i],false)
            binding.listview.visibility = View.GONE
        }
        binding.listview.adapter = listAdapter
    }

    override fun onFailure(message: String) {
        Toast.makeText(this@SearchActivity,"Sem resultados",Toast.LENGTH_SHORT).show()
    }


    //TICKET RECYCLER VIEW SEARCH

    override fun onPreExecuteQuery() {
    }

    override fun onResultQuery(tickets: List<Ticket>) {
        val adapter = TicketsAdapter(tickets) { it ->
            val intent = Intent(this@SearchActivity, TicketActivity::class.java)
            Log.i("ticketInfo",it)
            intent.putExtra("id", it)
            startActivity(intent)
        }
        binding.rvSearch.adapter = adapter
        binding.rvSearch.layoutManager = LinearLayoutManager(this@SearchActivity)
    }

    override fun onFailureQuery(message: String) {
        Toast.makeText(this@SearchActivity,"Não achou o ticket",Toast.LENGTH_SHORT).show()
    }
}
