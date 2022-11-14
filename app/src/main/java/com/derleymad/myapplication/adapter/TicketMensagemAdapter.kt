package com.derleymad.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.derleymad.myapplication.R
import com.derleymad.myapplication.model.Mensagem

class TicketMensagemAdapter(private val list : List<Mensagem>,private val myName : String) : RecyclerView.Adapter<TicketMensagemAdapter.GenericViewHolder>() {

    abstract class GenericViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(itemCurrent: Mensagem)
    }


    inner class SendViewHolder(itemView: View) : GenericViewHolder(itemView){
        override fun bind(itemCurrent: Mensagem) {
            val data = itemView.findViewById<TextView>(R.id.tv_date)
            val mensagem = itemView.findViewById<TextView>(R.id.tv_msg)
            data.text = itemCurrent.data
            mensagem.text = itemCurrent.mensagem
        }
    }

    inner class NormalViewHolder(itemView: View) : GenericViewHolder(itemView){
        override fun bind(itemCurrent: Mensagem) {
            val data = itemView.findViewById<TextView>(R.id.tv_date)
            val mensagem = itemView.findViewById<TextView>(R.id.tv_msg)
            val de = itemView.findViewById<TextView>(R.id.tv_de)
            data.text = itemCurrent.data
            mensagem.text = itemCurrent.mensagem
            de.text = itemCurrent.de
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        return (if (viewType==1){
            SendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.msg_item_send,parent,false))
        }else{
            NormalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.msg_item,parent,false))
        })
    }

    override fun onBindViewHolder(holder: GenericViewHolder , position: Int) {
        val itemCurrent = list[position]
        holder.bind(itemCurrent)
    }

    override fun getItemViewType(position: Int): Int {
        val itemCurrent = list[position]
        return if(itemCurrent.de.contains(myName)){
            1
        }else{
            0
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}