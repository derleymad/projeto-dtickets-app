package com.derleymad.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.derleymad.myapplication.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPreference =  getSharedPreferences("credentials", Context.MODE_PRIVATE)

        editor = sharedPreference.edit()
        if(sharedPreference.getBoolean("autologin",false)){
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
        }

        binding.contentMain.username.addTextChangedListener(watcher)
        binding.contentMain.password.addTextChangedListener(watcher)

        binding.contentMain.loginBtnEnter.setOnClickListener {
            if(checkNetwork()) {
                loginAuth(
                    binding.contentMain.username.text.toString(),
                    binding.contentMain.password.text.toString()
                )
            }else{
                Snackbar.make(binding.root,"Sem conexão com a internet",Snackbar.LENGTH_SHORT).show()

            }
        }
    }

//    private fun getDeepLinks() : String{
//        val appLinkIntent = intent
//        val appLinkData = appLinkIntent.data
//        val appHostUrl = appLinkData?.host
//
//        val paramsName = appLinkData?.queryParameterNames ?: listOf()
//        val id = appLinkData?.getQueryParameter(("id")) ?: ""
//
//        Log.i("teste",id.toString())
//
//        return id
//    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.contentMain.loginBtnEnter.isEnabled = s.toString().isNotEmpty()
        }
        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun checkNetwork() : Boolean{
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && (networkInfo.isAvailable || networkInfo.isConnected)
    }

    private fun loginAuth(username:String,password:String) {
        val url = "https://atendimento.ufca.edu.br/scp/login.php"
        binding.contentMain.progressBar.visibility = View.VISIBLE
        binding.contentMain.loginBtnEnter.visibility = View.INVISIBLE

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

            val invalidLogin : Boolean = doc.body().toString().contains("Invalid login")

            if(!invalidLogin){
                val myname=doc.body().select("#info strong").text()
                Log.i("myname",myname)
                editor.putString("myname",myname)
            }

            runOnUiThread {
                if(invalidLogin){
                    binding.contentMain.progressBar.visibility = View.GONE
                    binding.contentMain.loginBtnEnter.visibility = View.VISIBLE
                    binding.contentMain.loginTxtInputLayoutPassword.error =  "Usuário ou senha incorreta!"
                }else{
                    if(binding.contentMain.checkbox.isChecked){
                        editor.putBoolean("autologin",true)
                        editor.putString("username",binding.contentMain.username.text.toString())
                        editor.putString("password",binding.contentMain.password.text.toString())
                        editor.apply()
                    }else{
                        editor.putString("username",binding.contentMain.username.text.toString())
                        editor.putString("password",binding.contentMain.password.text.toString())
                        editor.apply()
                    }
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

}