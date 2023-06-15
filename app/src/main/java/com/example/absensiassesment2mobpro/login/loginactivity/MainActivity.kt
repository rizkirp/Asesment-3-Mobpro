package com.example.absensiassesment2mobpro.login.loginactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.absensiassesment2mobpro.BottomNavigationActivity
import com.example.absensiassesment2mobpro.databinding.ActivityMainBinding
import com.example.absensiassesment2mobpro.datastore.DataStoreUtil
import com.example.absensiassesment2mobpro.login.AuthViewModel
import com.example.absensiassesment2mobpro.login.registeractivity.RegisterActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Masuk Ke Aplikasi"

        val dataStoreUtil = DataStoreUtil(this)

        progressBar = binding.progressBar

        lifecycleScope.launch {
            dataStoreUtil.username.collectLatest {
                if (!it.isNullOrBlank()) {
                    val intent = Intent(this@MainActivity, BottomNavigationActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            val username = binding.usernameEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Data Tidak Lengkap", Toast.LENGTH_SHORT).show()
            } else {
                LoginAsyncTask().execute(username, password)
                /*lifecycleScope.launch {
                    viewModel.isExist(username).collectLatest {
                        if (it) {
                            viewModel.login(username).collectLatest { pass->
                                if (pass == password) {
                                    dataStoreUtil.saveSession(username)
                                    finish()
                                    val intent = Intent(this@MainActivity, BottomNavigationActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@MainActivity, "Kata sandi salah", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "Username Belum Terdaftar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }*/
            }
        }

        binding.registerTxt.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private inner class LoginAsyncTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()

            // Menampilkan indikator loading
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): String? {
            val username = params[0]
            val password = params[1]

            // Membangun URL login
            val urlString = "https://rikustudios.com/rikudev.my.id/projects/absensi/user.php"

            try {
                // Membuat koneksi HTTP
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                // Mengatur metode HTTP ke POST
                connection.requestMethod = "POST"
                connection.doOutput = true

                // Mengirim data login dalam format POST
                val postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8")+
                        "&action=login"

                // Mengirim data ke server
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes(postData)
                outputStream.flush()
                outputStream.close()

                // Membaca response dari server
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()
                connection.disconnect()

                return response.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            // Menyembunyikan indikator loading
            progressBar.visibility = View.GONE

            if (result != null) {
                try {
                    // Mengonversi response menjadi objek JSON
                    Log.e("result",result)
                    val json = JSONObject(result)

                    // Memeriksa status dari response
                    val status = json.optString("status", "")
                    val message = json.optString("message", "")

                    if (status == "success") {
                        // Login berhasil

                        val username = json.optJSONObject("data")!!.optString("username")
                        val fullName = json.optJSONObject("data")!!.optString("nama_lengkap")
                        onLoginSukses(username, fullName)
                        showToast(message)
                    } else {
                        // Login gagal
                        showToast(message)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // Terjadi kesalahan dalam koneksi atau response kosong
                showToast("Terjadi kesalahan")
            }
        }
    }

    fun onLoginSukses(username: String,fullName: String){
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("AbsensiSession", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("fullName", fullName)
        editor.putBoolean("isLogin", true)
        editor.apply()

        finish()
        val intent = Intent(this@MainActivity, BottomNavigationActivity::class.java)
        startActivity(intent)
    }

    fun showToast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

}