package com.derleymad.myapplication.ui.fragments.pager.dashboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.derleymad.myapplication.R
import com.derleymad.myapplication.databinding.FragmentOverviewBinding
import com.derleymad.myapplication.model.Overview
import com.derleymad.myapplication.utils.GetOverviewRequest
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.snackbar.Snackbar

class OverviewFragment : Fragment(), GetOverviewRequest.Callback {

    private lateinit var binding : FragmentOverviewBinding
    private lateinit var username : String
    private lateinit var password : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOverviewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreference =  view.context.getSharedPreferences("credentials", Context.MODE_PRIVATE)

        username = sharedPreference.getString("username","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )
        password = sharedPreference.getString("password","none")?: throw  java.lang.IllegalStateException(
            "Não devia estar aqui sem ter feito login!"
        )

        GetOverviewRequest(this@OverviewFragment).execute(username,password)
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onPreExecute() {
        binding.progressBar.visibility = View.VISIBLE
        binding.included.errorContainer.visibility = View.INVISIBLE
    }

    override fun onResult(overview: List<Overview>) {
        binding.progressBar.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE

        createPie(binding.chart,overview[0])
        binding.myName.text = overview[0].my_name

        createPie(binding.chartDepartamento,overview[1])
        binding.departamento.text = overview[1].my_name
    }

    override fun onFailure(message: String) {

        binding.included.errorContainer.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.included.retry.setOnClickListener {
            GetOverviewRequest(this@OverviewFragment).execute(username,password)
        }
//        Snackbar.make(binding.root,"Sem conexão", Snackbar.LENGTH_SHORT)
//            .show()
        Log.e("error_internet_overview",message)
    }

    fun createPie(pieChart : PieChart,overview: Overview){
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = overview.type == "pessoal"
        pieChart.setHoleColor(resources.getColor(R.color.transparent))
//        pieChart.setTransparentCircleColor(true)
//        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
//        pieChart.transparentCircleRadius = 61f

        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = true
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(overview.fechados.toFloat()))
        entries.add(PieEntry(overview.reabertos.toFloat()))
        entries.add(PieEntry(overview.atrasados.toFloat()))
        entries.add(PieEntry(overview.atribuidos.toFloat()))
        entries.add(PieEntry(overview.abertos.toFloat()))
        val dataSet = PieDataSet(entries, "Tickets")
        dataSet.setDrawIcons(true)
        dataSet.sliceSpace = 5f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.blue_enabled))
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.emergency_priority))
        colors.add(resources.getColor(R.color.high_priority))
        colors.add(resources.getColor(R.color.green_ufca))
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.invalidate()
    }
}