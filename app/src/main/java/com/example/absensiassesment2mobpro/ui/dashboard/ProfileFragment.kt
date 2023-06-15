package com.example.absensiassesment2mobpro.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.absensiassesment2mobpro.R
import com.example.absensiassesment2mobpro.databinding.FragmentProfileBinding
import com.example.absensiassesment2mobpro.datastore.DataStoreUtil
import com.example.absensiassesment2mobpro.login.loginactivity.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val dataStore = context?.let { DataStoreUtil(it) }
        var username = ""
        lifecycleScope.launch {
            dataStore?.username?.collectLatest {
                username = it ?: ""
            }
        }

        val sharedPreferences: SharedPreferences = requireContext().applicationContext.getSharedPreferences("AbsensiSession", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username","").toString()
        getUserData(username)

        /*viewModel.getUserDetail(username)?.observe(viewLifecycleOwner) {
            binding.fullnameTxt.text = it.fullName

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.birthdate
            binding.birthdateTxt.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(calendar.time)
            binding.genderTxt.text = if (it.gender == 0) "Laki Laki" else "Perempuan"
            binding.imageView.setImageResource(if (it.gender == 0) R.drawable.male else R.drawable.female)
            binding.usernameTxt.text = it.username
        }*/

        binding.logoutTxt.setOnClickListener {
           /* lifecycleScope.launch {
                dataStore?.deleteSession()
            }*/
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Fungsi untuk mengambil data pengguna berdasarkan nama pengguna (username)
    fun getUserData(username: String) {
        val client = OkHttpClient()
        val url =
            "https://rikustudios.com/rikudev.my.id/projects/absensi/user.php?username=$username" // Ganti dengan URL API yang sesuai

        val request = Request.Builder()
            .url(url)
            .build()

        showProgress(true)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Koneksi gagal atau ada kesalahan jaringan
                showProgress(false)
                showToast("Periksa Koneksi Internet Anda!")

            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                showProgress(false)

                try {
                    if (responseData != null) {
                        Log.e("get data", responseData)
                    }
                    val jsonObject = JSONObject(responseData)
                    val success = jsonObject.getString("status").equals("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val userData = jsonObject.getJSONObject("data")
                        val id = userData.getInt("id")
                        val fullName = userData.getString("nama_lengkap")
                        val username = userData.getString("username")
                        val birthDate = userData.getString("tanggal_lahir")
                        val gender = userData.getString("jenis_kelamin")

                        updateView(birthDate, gender,username, fullName)

                        showToast(message)

                    } else {
                        showToast(message)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()

                }
            }
        })
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

    fun updateView(birthDate:String, gender:String, username:String, fullName:String){
        Handler(Looper.getMainLooper()).post {
            binding.birthdateTxt.text = birthDate
            binding.genderTxt.text = if (gender == "0") "Laki-laki" else "Perempuan"
            showImg(gender)
            binding.usernameTxt.text = username
            binding.fullnameTxt.text = fullName

        }

    }

    fun showToast(message:String){
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showImg(gender: String) {
        var imageUrl = "https://rikustudios.com/rikudev.my.id/projects/absensi/img/female.png"
        if (gender == "0") {
            imageUrl = "https://rikustudios.com/rikudev.my.id/projects/absensi/img/male.png"
        }

        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.imageView)
    }
}