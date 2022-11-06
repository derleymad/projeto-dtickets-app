package com.derleymad.myapplication.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.derleymad.myapplication.ui.fragments.pager.AbertosFragment
import com.derleymad.myapplication.ui.fragments.pager.FechadosFragment
import com.derleymad.myapplication.ui.fragments.pager.MeusFragment
import com.derleymad.myapplication.ui.fragments.pager.RespondidosFragment

class PagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                MeusFragment()
            }
            1 -> {
                AbertosFragment()
            }
            2 -> {
                RespondidosFragment()
            }
            3 -> {
                FechadosFragment()
            }
            else -> {
                throw Resources.NotFoundException("Posição nao foi achada!")
            }
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}
