package com.derleymad.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.derleymad.myapplication.R
import com.derleymad.myapplication.model.Mensagem

class TicketMensagemAdapter(private val list : List<Mensagem>) : RecyclerView.Adapter<TicketMensagemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemCurrent = list[position]
        holder.bind(itemCurrent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder (v: View): RecyclerView.ViewHolder(v) {
        fun bind(itemCurrent : Mensagem){
            val data = itemView.findViewById<TextView>(R.id.tv_date)
            val mensagem = itemView.findViewById<TextView>(R.id.tv_msg)
            val de = itemView.findViewById<TextView>(R.id.tv_de)

            data.text = itemCurrent.data
            mensagem.text = itemCurrent.mensagem
            de.text = itemCurrent.de
        }
    }


}