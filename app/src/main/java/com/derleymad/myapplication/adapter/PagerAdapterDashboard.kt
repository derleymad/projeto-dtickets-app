package com.derleymad.myapplication.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.derleymad.myapplication.ui.fragments.pager.dashboard.OverviewFragment
import com.derleymad.myapplication.ui.fragments.pager.dashboard.ProductivityFragment

class PagerAdapterDashboard(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                OverviewFragment()
            }
            1 -> {
                ProductivityFragment()
            }
            else -> {
                throw Resources.NotFoundException("Posição nao foi achada!")
            }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}
