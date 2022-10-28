package com.derleymad.myapplication.ui

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
import com.derleymad.myapplication.adapter.TicketsRespondidosAdapter
import com.derleymad.myapplication.model.Ticket


class RespondidosFragment (val list: List<Ticket>,
                           ): Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_respondidos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rv_respondidos)
        rv.adapter = TicketsRespondidosAdapter(list) { it ->
            val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        }
        rv.layoutManager = LinearLayoutManager(view.context)
    }


}