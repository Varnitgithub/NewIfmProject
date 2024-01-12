package com.example.ifmapp.activities

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.Locationmodel
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityCheckInScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import java.io.IOException
import java.util.Locale

class CheckInScreen : AppCompatActivity() {
    private lateinit var realtimeLocationLiveData: MutableLiveData<Locationmodel>
    private lateinit var binding: ActivityCheckInScreenBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_screen)
        realtimeLocationLiveData = MutableLiveData()
        binding.finalLayoutCL.visibility = View.GONE
        binding.checkinCL.visibility = View.VISIBLE

        binding.btnSubmit.setOnClickListener {
            binding.checkinCL.visibility = View.GONE

            binding.finalLayoutCL.visibility = View.VISIBLE
            realtimeLocationLiveData.observe(this) {

                binding.latlgTxt.text = "${it.latitude} ${it.longitude}"
                getAddressFromLocation(this,it.latitude,it.longitude)
            }
        }

        binding.btnCross.setOnClickListener {
            startActivity(Intent(this,DashBoardScreen::class.java))
        }


        realtimeLocationLiveData.observe(this) {

            binding.latlgTxt.text = "${it.latitude} ${it.longitude}"
            getAddressFromLocation(this,it.latitude,it.longitude)
        }
    }



    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                binding.address.text = "${address.locality} ${address.subLocality} ${address.adminArea} ${address.subAdminArea}"

            }
        } catch (e: IOException) {
            Log.e("Geocoding", "Error getting address from location: $e")
        }

        return "Address not found"
    }
}