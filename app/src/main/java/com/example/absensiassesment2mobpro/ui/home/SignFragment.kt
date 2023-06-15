package com.example.absensiassesment2mobpro.ui.home

import android.app.AlertDialog
import android.content.Context
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensiassesment2mobpro.R
import com.example.absensiassesment2mobpro.databinding.FragmentSignBinding
import com.example.absensiassesment2mobpro.datastore.DataStoreUtil
import com.example.absensiassesment2mobpro.room.absensi.Absensi
import com.google.android.material.color.utilities.Score.score
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class SignFragment : Fragment(), DeleteClick {

    private var _binding: FragmentSignBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SignViewModel

    var username = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SignViewModel::class.java]

        binding.absensiRv.layoutManager = LinearLayoutManager(context)

        val dataStore = context?.let { DataStoreUtil(it) }

        lifecycleScope.launch {
            dataStore?.username?.collectLatest {
                if (it != null) {
                    username = it
                }
            }
        }

        val sharedPreferences: SharedPreferences = requireContext().applicationContext.getSharedPreferences("AbsensiSession", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username","").toString()

        /*viewModel.allAbsensi()?.observe(viewLifecycleOwner) {
            binding.absensiRv.adapter = SignAdapter(it, viewModel, username, this)
        }*/

        refreshData()


        binding.absensiBtn.setOnClickListener {
            findNavController().navigate(R.id.toAddSign)
        }
    }

    fun refreshData(){
        getAbsensiData(username) { success, data, message  ->
            if (success) {
                // Registrasi berhasil
                showProgress(false)
                Handler(Looper.getMainLooper()).post {
                    binding.absensiRv.adapter = SignAdapter(data!!, viewModel, username, this)
                }
                showToast(message)
                Log.e("response", message)
            } else {
                // Registrasi gagal
                showProgress(false)
                showToast(message)
                Log.e("response", message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(absensi: Absensi) {
        val alert = AlertDialog.Builder(context)
        alert.setMessage("Apakah anda yakin ingin menghapus absensi ini?")
        alert.setPositiveButton("Ya") { dialog, _ ->
            sendDeleteRequest(absensi.id);
            dialog.dismiss()
        }

        alert.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }

    fun getAbsensiData(username: String, callback: (success: Boolean, data: List<Absensi>?, message: String) -> Unit) {
        val url = "https://rikustudios.com/rikudev.my.id/projects/absensi/absensi.php"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        showProgress(true)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Koneksi gagal atau error
                callback(false, null, "Failed to fetch absensi data: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    Log.e("response", responseData)
                    try {
                        val absensiList = parseAbsensiData(responseData)
                        callback(true, absensiList, "Absensi data fetched successfully")
                    } catch (e: JSONException) {
                        // Gagal parsing data JSON
                        callback(false, null, "Failed to parse absensi data: ${e.message}")
                    }
                } else {
                    // Respons tidak sukses
                    callback(false, null, "Failed to fetch absensi data: ${response.code}")
                }
            }
        })
    }

    fun parseAbsensiData(responseData: String): List<Absensi> {
        val absensiList = mutableListOf<Absensi>()

        val jsonObject = JSONObject(responseData)
        val jsonArray = jsonObject.getJSONArray("data")
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getInt("id")
            val username = jsonObject.getString("nama")
            val nama_lengkap = jsonObject.getString("nama_lengkap")
            val tanggalAbsen = jsonObject.getString("tanggal_absen")
            val waktuAbsen = jsonObject.getString("waktu_absen")
            val keterangan = jsonObject.getString("keterangan")
            Log.e("set id", id.toString());
            val absensi = Absensi(id, username, nama_lengkap,keterangan, tanggalAbsen, waktuAbsen)
            absensiList.add(absensi)
        }

        return absensiList
    }

    private fun sendDeleteRequest(id: Int) {
        val client = OkHttpClient()
        Log.e("id",id.toString())
        val url ="https://rikustudios.com/rikudev.my.id/projects/absensi/absensi.php?aksi=delete&&id="+id // Ganti dengan URL API yang sesuai
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
                        refreshData()

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

    fun showToast(message: String){
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

    }
}