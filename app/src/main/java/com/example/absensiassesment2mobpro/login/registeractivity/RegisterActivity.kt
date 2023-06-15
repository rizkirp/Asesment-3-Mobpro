package com.example.absensiassesment2mobpro.login.registeractivity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import com.example.absensiassesment2mobpro.databinding.ActivityRegisterBinding
import com.example.absensiassesment2mobpro.login.AuthViewModel
import com.example.absensiassesment2mobpro.login.loginactivity.MainActivity
import com.example.absensiassesment2mobpro.room.auth.Auth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()

        supportActionBar?.title = "Silahkan Daftar"

        binding.birthDateEdt.setOnClickListener {
            DatePickerDialog(this, { view, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val simple = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                binding.birthDateEdt.setText(simple.format(calendar.time))
            },calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()
        }

        binding.registerBtn.setOnClickListener {
            val fullname = binding.fullNameEdt.text.toString()
            val username = binding.usernameEdt.text.toString()
            val password = binding.passwordEdt.text.toString()
            val birthDate = calendar.timeInMillis
            val radioButton = findViewById<RadioButton>(binding.radioGr.checkedRadioButtonId)
            val gender = if (radioButton.text.toString() == "Laki Laki") {
                0
            } else {
                1
            }

            if (fullname.isBlank() || username.isBlank() ||password.isBlank() || binding.birthDateEdt.text.toString().isBlank() || binding.radioGr.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(username, password, fullname, birthDate.toString(), gender.toString()) { success, message ->
                    if (success) {
                        // Registrasi berhasil
                        showProgress(false)
                        if (message != null) {
                            showToast(message)
                        }
                        finish()
                    } else {
                        // Registrasi gagal
                        showProgress(false)
                        if (message != null) {
                            showToast(message)
                        }
                    }
                }
            }
        }

        binding.loginTxt.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Fungsi untuk melakukan registrasi pengguna
    fun registerUser(username: String, password: String, fullName: String, birthDate: String, gender: String, callback: (Boolean, String?) -> Unit) {
        val client = OkHttpClient()
        val url = "https://rikustudios.com/rikudev.my.id/projects/absensi/user.php" // Ganti dengan URL API yang sesuai

        showProgress(true)

        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("nama_lengkap", fullName)
            .add("tanggal_lahir", birthDate)
            .add("jenis_kelamin", gender)
            .add("action","register")
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Koneksi gagal atau ada kesalahan jaringan
                callback(false, "Connection error")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                try {

                    val jsonObject = JSONObject(responseData)
                    val success = jsonObject.getString("status").equals("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        callback(true, message)
                    } else {
                        callback(false, message)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false, "JSON parsing error")
                }
            }
        })
    }

    fun showToast(message: String){
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showProgress(value: Boolean){
        Handler(Looper.getMainLooper()).post {
            if(value)
                binding.progressBar2.visibility = View.VISIBLE
            else{
                binding.progressBar2.visibility = View.GONE
            }
        }

    }
}