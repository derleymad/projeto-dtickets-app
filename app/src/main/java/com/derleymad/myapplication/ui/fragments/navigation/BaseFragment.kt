package com.derleymad.myapplication.ui.fragments.navigation

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.derleymad.myapplication.R
import com.derleymad.myapplication.adapter.PagerAdapter
import com.derleymad.myapplication.databinding.FragmentBaseBinding
import com.google.android.material.tabs.TabLayoutMediator

class BaseFragment : Fragment() {

    private var _binding : FragmentBaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.title = "Home"
        _binding = FragmentBaseBinding.inflate(inflater,container,false)
        setupTabLayout()
        return binding.root

    }

    private fun setupTabLayout() {
        binding.apply {
            viewPager.adapter = PagerAdapter(requireActivity())
            viewPager.offscreenPageLimit = 4
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                0 -> "Meus"
                1 -> "Abertos"
                2 -> "Respondidos"
                3 -> "Fechados"
                else -> throw  Resources.NotFoundException("Posição não encontrada!")
            }
            tab.setIcon(
                when (position) {
                    0 -> R.drawable.ic_baseline_person_24
                    1 -> R.drawable.ic_baseline_folder_open_24
                    2 -> R.drawable.ic_baseline_draw_24
                    3 -> R.drawable.ic_baseline_done_all_24
                    else -> throw  Resources.NotFoundException("Posição não encontrada!")
                }
            )
        }.attach()
        }
    }
}