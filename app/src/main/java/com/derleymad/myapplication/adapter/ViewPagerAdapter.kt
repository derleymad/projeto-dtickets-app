package com.derleymad.myapplication.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.derleymad.myapplication.ui.AbertosFragment
import com.derleymad.myapplication.ui.FechadosFragment
import com.derleymad.myapplication.ui.MeusFragment
import com.derleymad.myapplication.ui.RespondidosFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

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


}