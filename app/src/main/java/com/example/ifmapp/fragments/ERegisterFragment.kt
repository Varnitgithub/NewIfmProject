package com.example.ifmapp.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import android.content.pm.PackageManager
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.FragmentEregisterBinding
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.GlobalLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ERegisterFragment(private var pin: String, private var empId: String) : Fragment() {
    private lateinit var binding: FragmentEregisterBinding
    private var empName: String? = null
    private var userDesignation: String? = null
    private lateinit var siteList: ArrayList<String>
    private var clickedImageBitmap: Bitmap? = null
    private var siteSelect: String? = null
    private var locationAutoid: String? = null
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val CAMERA_CAPTURE_REQUEST_CODE = 101
    private lateinit var retrofitInstance: ApiInterface

    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap?
                clickedImageBitmap = imageBitmap
                binding.addPhotoImage.setImageBitmap(imageBitmap)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_eregister, container, false)
        Log.d("TAGGGGGGGG", "onCreate: e_register screen...............$empId")
        siteList = ArrayList()
        retrofitInstance = RetrofitInstance.apiInstance
        for (users in SaveUsersInSharedPreference.getList(requireContext())) {
            if (users.empId == empId) {

                empName = users.userName
                pin = users.pin
                locationAutoid = users.LocationAutoId
            }
        }
        binding.addPhotoTxtBtnLL.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        binding.btnSubmit.setOnClickListener {
            /*"sams",
            empId,
            empName.toString(),
            "",
            locationAutoid.toString(),
            siteSelect.toString(),
            binding.visitorNameEdittext.text.toString().trim(),
            binding.visitorPurpose.text.toString().trim(),
            binding.visitorContactNumber.text.toString().trim()*/
            if (empName!=null
                && empId.isNotEmpty()
                && locationAutoid!=null&&
                siteList.isNotEmpty()&&  binding.visitorNameEdittext.text.toString().trim().isNotEmpty()&&
                binding.visitorPurpose.text.toString().trim().isNotEmpty()&&
                binding.visitorContactNumber.text.toString().trim().isNotEmpty()){

                insertRegisterEntry()

            }else{
                CustomToast.showToast(requireContext(),"Please enter all details")
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getSitesFromServer(
            locationAutoid.toString(),
            GlobalLocation.location.latitude,
            GlobalLocation.location.longitude,
            empId
        )
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                // Handle permission denied
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }


    private fun insertRegisterEntry(){
        retrofitInstance.insertRegisterEntry(
            "sams",
            empId,
            empName.toString(),
            "",
            locationAutoid.toString(),
            siteSelect.toString(),
            binding.visitorNameEdittext.text.toString().trim(),
            binding.visitorPurpose.text.toString().trim(),
            binding.visitorContactNumber.text.toString().trim()
        ).enqueue(object : Callback<VerifyOtpResponse?> {
            override fun onResponse(
                call: Call<VerifyOtpResponse?>,
                response: Response<VerifyOtpResponse?>
            ) {
                if (response.isSuccessful){


                    if (response.body()?.get(0)?.MessageID?.toInt()==1)
                    {
                        CustomToast.showToast(requireContext(), response.body()?.get(0)!!.MessageString)

                    }else{
                        CustomToast.showToast(requireContext(), response.body()?.get(0)!!.MessageString)
                    }
                }else{
                    CustomToast.showToast(requireContext(),"registration failed")
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
            }
        })
    }

    fun setSiteSelection(
        userId: String,
        sites: ArrayList<String>
    ) {
        val _SiteList = ArrayList<String>()
        _SiteList.clear()
        _SiteList.addAll(sites)

        val adapterSelectionShift = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            ArrayList<String>()
        )

        for (site in SaveUsersInSharedPreference.getCurrentUserShifts(requireActivity(), userId)) {
            if (sites.contains(site.site)) {
                _SiteList.clear()
                _SiteList.add(site.site)
            }
        }
        adapterSelectionShift.addAll(_SiteList)

        adapterSelectionShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectSite.adapter = adapterSelectionShift

        binding.spinnerSelectSite.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    siteSelect = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {
                            siteSelect = it.getItemAtPosition(0).toString()
                        }
                    }
                }
            }
    }

    private fun getSitesFromServer(
        locationAutoid: String,
        latitude: String,
        longitude: String,
        userId: String
    ) {
        siteList.clear()
        retrofitInstance.getGeoMappedSites(
            "sams", locationAutoid, latitude,
            longitude
        ).enqueue(object : Callback<GeoMappedResponse?> {
            override fun onResponse(
                call: Call<GeoMappedResponse?>,
                response: Response<GeoMappedResponse?>
            ) {
                if (response.isSuccessful) {

                    val sizes = response.body()?.size?.minus(1)
                    for (i in 0..sizes!!) {
                        siteList.add(response.body()!!.get(i).ClientCode)
                    }
                    Log.d("TAGGGGGGGGGGGGG", "onResponse: this is site list..............$siteList")
                    setSiteSelection(userId, siteList)
                }
            }

            override fun onFailure(
                call: Call<GeoMappedResponse?>,
                t: Throwable
            ) {

            }
        })
    }
}



