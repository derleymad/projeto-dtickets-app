package com.derleymad.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.derleymad.myapplication.R
import com.derleymad.myapplication.model.Ticket

class TicketsRespondidosAdapter(private val list : List<Ticket>,
                                private var onTicketClickListener : (String) -> Unit,
) : RecyclerView.Adapter<TicketsRespondidosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemCurrent = list[position]
        holder.bind(itemCurrent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        fun bind(itemCurrent : Ticket){
            val description = itemView.findViewById<TextView>(R.id.description)
            val number = itemView.findViewById<TextView>(R.id.number)
            val prioridade = itemView.findViewById<TextView>(R.id.priority)
            val prioridadeBackground = itemView.findViewById<ConstraintLayout>(R.id.background_priority)
            val de = itemView.findViewById<TextView>(R.id.de)
            val para = itemView.findViewById<TextView>(R.id.para)
            val data = itemView.findViewById<TextView>(R.id.data)

            description.text = itemCurrent.assunto
            number.text = itemCurrent.numero
            de.text = itemCurrent.de
            para.text = itemCurrent.para
            data.text = itemCurrent.data
            prioridade.text = itemCurrent.prioridade
            prioridadeBackground.setBackgroundColor(when(itemCurrent.prioridade){
                "Emergency" -> itemView.resources.getColor(R.color.emergency_priority)
                "High" -> itemView.resources.getColor(R.color.high_priority)
                "Normal" -> itemView.resources.getColor(R.color.normal_priority)
                else -> itemView.resources.getColor(R.color.white)
                 })

            itemView.findViewById<CardView>(R.id.container_card_view).setOnClickListener {
                onTicketClickListener?.invoke(itemCurrent.id)
            }

        }
    }


}