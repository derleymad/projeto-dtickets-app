package com.derleymad.myapplication

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.derleymad.myapplication.adapter.ViewPagerAdapter
import com.derleymad.myapplication.databinding.ActivityMainBinding
import com.derleymad.myapplication.model.Ticket
import com.google.android.material.tabs.TabLayoutMediator
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var username : String
    private lateinit var password : String
    val tickets = mutableListOf<List<Ticket>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference =  getSharedPreferences("credentials", Context.MODE_PRIVATE)

        username = sharedPreference.getString("username","noen").toString()
        password = sharedPreference.getString("password","none").toString()
        loginAndGetTickets(username,password)
    }

        fun loginAndGetTickets(username: String,password: String) {
        val url =
            "https://atendimento.ufca.edu.br/scp/login.php"
        val urlAbertos =
            "https://atendimento.ufca.edu.br/scp/tickets.php?sort=date&order=DESC&"
        val urlMeus =
            "https://atendimento.ufca.edu.br/scp/tickets.php?sort=date&order=DESC&status=assigned"
        val urlFechados =
            "https://atendimento.ufca.edu.br/scp/tickets.php?status=closed"
        val urlRespondidos =
            "https://atendimento.ufca.edu.br/scp/tickets.php?sort=date&order=DESC&status=answered"

        val links = mutableListOf(urlMeus,urlAbertos,urlRespondidos,urlFechados)

        try {
            Thread{
                val loginForm =
                    Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .execute()

                val doc: Document = Jsoup.connect(url)
                    .data("userid", "$username")
                    .data("passwd", "$password")
                    .cookies(loginForm.cookies())
                    .post()

                for (i in links){
                    val ticketAtual = mutableListOf<Ticket>()
                    val page= Jsoup
                        .connect(i)
                        .cookies(loginForm.cookies())
                        .get()

                    val table : Elements = page.select("table")[1].select("tbody").select("tr")

                    val id = table.select("td:nth-child(1)").select("input").eachAttr("value")
                    val number = table.select("td:nth-child(2)").select("a").eachText()
                    val email = table.select("td:nth-child(2)").eachAttr("title")
                    val data = table.select("td:nth-child(3)").eachText()
                    val assunto = table.select("td:nth-child(4)").select("a").eachText()
                    val de = table.select("td:nth-child(5)").eachText()
                    val prioridade = table.select("td:nth-child(6)").eachText()
                    val para = table.select("td:nth-child(7)").eachText()

                    for (i in 0 until table.size){
                        ticketAtual.add(
                            Ticket(
                            id=id[i],
                            numero=number[i],
                            email=email[i],
                            data=data[i],
                            assunto = assunto[i],
                            de = de[i],
                            prioridade = prioridade[i],
                            para = para[i])
                        )
                    }
                    tickets.add(ticketAtual)
                }
                    runOnUiThread {
                        setUpTabs(tickets[0],tickets[1],tickets[2],tickets[3])
                }
            }.start()
        }catch (e : NetworkErrorException){
            println(e.message)
        }
    }

    private fun setUpTabs(
        listMeus: List<Ticket>,
        listAbertos: List<Ticket>,
        listRespondidos: List<Ticket>,
        listFechados: List<Ticket>,
    ) {
        binding.progressBar.visibility = View.GONE
        binding.viewPager.adapter?.notifyDataSetChanged()
        binding.viewPager.adapter =
            ViewPagerAdapter(
                this@MainActivity,
                listMeus,
                listAbertos,
                listRespondidos,
                listFechados
            )
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, index ->
            tab.text = when (index) {
                0 -> "Meus (${listMeus.size})"
                1 -> "Abertos (${listAbertos.size})"
                2 -> "Respondidos (${listRespondidos.size})"
                3 -> "Fechados (${listFechados.size})"
                else -> throw  Resources.NotFoundException("Posição não encontrada!")
            }
            tab.setIcon(
                when (index) {
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