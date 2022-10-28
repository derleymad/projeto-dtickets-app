package com.derleymad.myapplication.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.derleymad.myapplication.TicketActivity
import com.derleymad.myapplication.adapter.TicketsRespondidosAdapter
import com.derleymad.myapplication.databinding.FragmentMeusBinding
import com.derleymad.myapplication.model.Ticket


class MeusFragment(val listMeus: List<Ticket>,
                   ) : Fragment() {

    private lateinit var binding : FragmentMeusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMeusBinding.inflate(layoutInflater)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMeus.adapter= TicketsRespondidosAdapter(listMeus) { it ->
            val intent = Intent(context, TicketActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        }
        binding.rvMeus.layoutManager = LinearLayoutManager(view.context)

    }

}