package com.derleymad.myapplication.ui.fragments.navigation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.derleymad.myapplication.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreference =  view.context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        binding.switch1.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                sharedPreference.edit().putBoolean("isInteract",true).apply()
            }else{
                sharedPreference.edit().putBoolean("isInteract",false).apply()
            }

        }

        binding.switch1.isChecked = sharedPreference.getBoolean("isInteract",false)

        val username = sharedPreference.getString("username","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        super.onViewCreated(view, savedInstanceState)
    }


}