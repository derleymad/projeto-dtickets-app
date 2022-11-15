package com.derleymad.myapplication.ui.fragments.navigation

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
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
//        (activity as AppCompatActivity).supportActionBar?.title = "Home"
        _binding = FragmentBaseBinding.inflate(inflater,container,false)
        setupTabLayout()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost : MenuHost = requireActivity()
        val bar = (activity as AppCompatActivity)
        bar.setSupportActionBar(binding.toolbar)

        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.menu_search -> {
                        Toast.makeText(requireContext(),"Search",Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun setupTabLayout() {
        binding.apply {
            viewPager.adapter = PagerAdapter(requireActivity())
            viewPager.offscreenPageLimit = 3
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                0 -> "Meus"
                1 -> "Em aberto"
                2 -> "Fechados"
                else -> throw  Resources.NotFoundException("Posição não encontrada!")
            }
            tab.setIcon(
                when (position) {
                    0 -> R.drawable.ic_baseline_person_24
                    1 -> R.drawable.ic_baseline_folder_open_24
                    2 -> R.drawable.ic_baseline_done_all_24
                    else -> throw  Resources.NotFoundException("Posição não encontrada!")
                }
            )
        }.attach()
        }
    }
}