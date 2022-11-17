package com.derleymad.myapplication.ui.fragments.navigation

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.derleymad.myapplication.R
import com.derleymad.myapplication.adapter.PagerAdapterDashboard
import com.derleymad.myapplication.databinding.FragmentDashboardBinding
import com.google.android.material.tabs.TabLayoutMediator

class DashboardFragment : Fragment() {
    private var _binding : FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)
        setupTabLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreference =  view.context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val myname = sharedPreference.getString("myname","Bem-vindo(a)")
        binding.nameOfPerson.text = myname

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupTabLayout() {
        binding.apply {
            viewPager.adapter = PagerAdapterDashboard(requireActivity())
            viewPager.offscreenPageLimit = 4
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Fixados"
                    1 -> "Overview"
                    else -> throw  Resources.NotFoundException("Posição não encontrada!")
                }
                tab.setIcon(
                    when (position) {
                        0 -> R.drawable.ic_baseline_push_pin_24
                        1 -> R.drawable.ic_baseline_person_24
                        else -> throw  Resources.NotFoundException("Posição não encontrada!")
                    }
                )
            }.attach()
        }
    }
}