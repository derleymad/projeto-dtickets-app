package com.derleymad.myapplication.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.derleymad.myapplication.ui.AbertosFragment
import com.derleymad.myapplication.ui.FechadosFragment
import com.derleymad.myapplication.ui.MeusFragment
import com.derleymad.myapplication.ui.RespondidosFragment
import com.derleymad.myapplication.model.Ticket

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    val listMeus: List<Ticket>,
    val listAbertos: List<Ticket>,
    val listRespondidos: List<Ticket>,
    val listFechados: List<Ticket>

) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                MeusFragment(listMeus )
            }
            1 -> {
                AbertosFragment(listAbertos)
            }
            2 -> {
                RespondidosFragment(listRespondidos)
            }
            3 -> {
                FechadosFragment(listFechados)
            }
            else -> {
                throw Resources.NotFoundException("Posição nao foi achada!")
            }
        }
    }


}