package com.example.ifmapp.activities.checklists

import CheckListAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.checklists.checklist__model.CheckListModel
import com.example.ifmapp.activities.checklists.checklist__model.CheckListModelItem
import com.example.ifmapp.activities.checklists.checklist__model.ImageAddingModel
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityCheckListForHousekeepingBinding
import com.example.ifmapp.modelclasses.header_list_response_model.HeaderResponseModel
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.UserObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CheckListForHousekeeping : AppCompatActivity(), CheckListAdapter.Clicked {
    private var CAMERA_PERMISSION_REQUEST_CODE = 111
    private var empId: String? = null
    private var empName: String? = null
    private var pin: String? = null
    private var siteSelect: String? = null
    private var tourCode: String? = null
    private var mPosition: String? = null
    private var headerId: String? = null
    private var position: String? = null
    private var tourCodes: String? = null

    private lateinit var headerIDSelect: ArrayList<Pair<String, String>>
    private lateinit var headerList: ArrayList<String>
    private var headerSelect: String? = null
    private var addPhotoPosition = ""

    private lateinit var retrofitInstance: ApiInterface
    private lateinit var binding: ActivityCheckListForHousekeepingBinding
    private lateinit var checkListAdapter: CheckListAdapter

    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap?
                if (imageBitmap != null) {
                    addPhotoApi(imageBitmap)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_check_list_for_housekeeping)
        binding = DataBindingUtil.setContentView(
            this@CheckListForHousekeeping,
            R.layout.activity_check_list_for_housekeeping
        )
        headerList = ArrayList()
        headerIDSelect = ArrayList()
        empId = intent.getStringExtra("empId")
        headerId = intent.getStringExtra("headerId")
        Log.d("HEADER", "onResume: this is headerId $headerId")
        siteSelect = intent.getStringExtra("siteSelect")
        position = intent.getStringExtra("position")
        tourCode = intent.getStringExtra("tourCode")
        mPosition = intent.getStringExtra("position")
        Log.d("TAGGGGGGGG", "onCreate:tour code... $tourCode")
        Log.d("TAGGGGGGGG", "onCreate:tour code... $siteSelect")
        retrofitInstance = RetrofitInstance.apiInstance
        if (siteSelect != null) {
            getHeaderFromServer(siteSelect.toString())
        }

        for (users in SaveUsersInSharedPreference.getList(this@CheckListForHousekeeping)) {
            if (users.empId == UserObject.userId) {
                empId = users.empId
                empName = users.userName
                pin = users.pin
            }
        }
        retrofitInstance = RetrofitInstance.apiInstance
        binding.houseKeepingRecyclerView.layoutManager = LinearLayoutManager(this)



        if (SaveUsersInSharedPreference.getHindiEnglish(this@CheckListForHousekeeping) == "hindi") {
        } else {
        }

    }

    override fun onAddPhotoClick(checkListModel: CheckListModelItem, position: Int) {
        if (checkCameraPermission()) {
            openCamera()
            addPhotoPosition = checkListModel.ChecklistAutoID
        } else {
            requestCameraPermission()
        }
    }

    override fun onViewPhotoClick(checkListModel: CheckListModelItem, position: Int) {
        var intent = Intent(this@CheckListForHousekeeping, ViewHouseKeepingPhoto::class.java)
        intent.putExtra("tourCode", tourCode)
        intent.putExtra("siteSelect", siteSelect)
        intent.putExtra("headerSelect", headerSelect)
        intent.putExtra("photoPosition", checkListModel.ChecklistAutoID)
        Log.d("TAGGGGGGGGGGGGG", "onViewPhotoClick: ${position + 1}")
        startActivity(intent)
    }

    fun setSiteSelection(
        sites: ArrayList<String>
    ) {
        val _SiteList = ArrayList<String>()
        _SiteList.clear()
        _SiteList.addAll(sites)

        val adapterSelectionShift = ArrayAdapter(
            this@CheckListForHousekeeping,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            ArrayList<String>()
        )
        adapterSelectionShift.addAll(_SiteList)

        adapterSelectionShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectSite.adapter = adapterSelectionShift

        binding.spinnerSelectSite.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position)
                    if (selectedItem != null) {
                        var mSelectedheaderId = getClientHeaderBySiteName(selectedItem.toString())
                        headerSelect = mSelectedheaderId

                        if (siteSelect != null && tourCode != null && headerSelect!=null) {

                            getCheckList(siteSelect ?: "", tourCode ?: "", headerSelect.toString())
                            checkListAdapter = CheckListAdapter(this@CheckListForHousekeeping,
                                this@CheckListForHousekeeping, siteSelect.toString(),
                                tourCode.toString(),headerSelect.toString())

                            binding.houseKeepingRecyclerView.adapter = checkListAdapter
                        }
                        Log.d("TAGGGGG", "onItemSelected: this condition $mSelectedheaderId")
                    } else {
                        Log.d("TAGGGGG", "onItemSelected: this condition null")

                        siteSelect == ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {

                        }
                    }
                }
            }
    }


    private fun getHeaderFromServer(
        selectSite: String
    ) {
        headerList.clear()
        retrofitInstance.getChecklistHeader(
            "sams", selectSite
        ).enqueue(object : Callback<HeaderResponseModel?> {
            override fun onResponse(
                call: Call<HeaderResponseModel?>,
                response: Response<HeaderResponseModel?>
            ) {
                if (response.isSuccessful) {
                    val sizes = response.body()?.size?.minus(1)
                    for (i in 0..sizes!!) {
                        headerList.add(response.body()!!.get(i).ChecklistHeader)
                        headerIDSelect.add(
                            response.body()?.get(i)!!.ChecklistHeader to response.body()
                                ?.get(i)!!.ChecklistHeaderAutoID
                        )
                    }
                    Log.d("TAGGGG", "onResponse headerlist: $headerList")
                    setSiteSelection(headerList)
                }
            }

            override fun onFailure(
                call: Call<HeaderResponseModel?>,
                t: Throwable
            ) {

            }
        })
    }

    fun getClientHeaderBySiteName(headerName: String): String? {
        for (pair in headerIDSelect) {
            if (pair.first == headerName) {
                return pair.second // Return ClientCode if ClientSiteName matches
            }
        }
        return null // Return null if no match is found
    }

    override fun onBackPressed() {
        val intent = Intent(this@CheckListForHousekeeping, MainActivity::class.java)
        intent.putExtra("mPIN", pin)
        intent.putExtra("empName", empName)
        intent.putExtra("empId", empId)
        startActivity(intent)
        super.onBackPressed()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun addPhotoApi(base64Image: Bitmap) {
        val currentTime = getCurrentTimeFormatted()
        val imageIntoBase64 = bitmapToBase64(base64Image)

        Log.d(
            "ADDPHOTO",
            "viewPhoto: TAGGG................1 ${headerSelect} 2 ${UserObject.userNames}" +
                    "3 ${UserObject.locationAutoId} 4 ${siteSelect.toString()} 5 ${currentTime}" +
                    " 6 ${tourCode.toString()} 7 $addPhotoPosition"
        )

        if (
            addPhotoPosition.isNotEmpty() &&
            imageIntoBase64.isNotEmpty() &&
            siteSelect.toString().isNotEmpty()&&
            tourCode.toString().isNotEmpty()&&headerSelect.toString().isNotEmpty()
        ) {
            retrofitInstance.insertCheckListImageHouseKeeping(
                "sams",siteSelect.toString(),tourCode.toString(),headerSelect.toString(),addPhotoPosition,imageIntoBase64
            ).enqueue(object : Callback<ImageAddingModel?> {
                override fun onResponse(
                    call: Call<ImageAddingModel?>,
                    response: Response<ImageAddingModel?>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.get(0)?.MessageID?.toInt() == 1) {
                            CustomToast.showToast(
                                this@CheckListForHousekeeping,
                               "Success"
                            )
                            Log.d("TARRRRRRRRRRRR", "onResponse: response success")
                        } else {
                            CustomToast.showToast(
                                this@CheckListForHousekeeping,
                               "Failed"
                            )
                        }
                    } else {
                        CustomToast.showToast(
                            this@CheckListForHousekeeping,
                            "Response not successful"
                        )
                    }
                }

                override fun onFailure(call: Call<ImageAddingModel?>, t: Throwable) {
                    CustomToast.showToast(
                        this@CheckListForHousekeeping,
                        "Response Failed"
                    )
                }
            })
        } else {
            CustomToast.showToast(this@CheckListForHousekeeping, "Please enter all details")
        }

    }

    fun getCurrentTimeFormatted(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@CheckListForHousekeeping,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this@CheckListForHousekeeping,
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

   /* private fun makeStatusComplete() {
        val currentTime = getCurrentTimeFormatted()


        retrofitInstance.updateChecklistStatustoCompletedHouseKeeping(
            "sams",
            UserObject.userId,
            UserObject.userNames,
            mPosition.toString(),
            UserObject.locationAutoId,
            siteSelect.toString(),
            tourCode.toString()
        ).enqueue(object : Callback<VerifyOtpResponse?> {
            override fun onResponse(
                call: Call<VerifyOtpResponse?>,
                response: Response<VerifyOtpResponse?>
            ) {
                if (response.isSuccessful) {

                    if (response.body()?.get(0)?.MessageID?.toInt() == 1) {
                        CustomToast.showToast(
                            this@CheckListForHousekeeping,
                            response.body()?.get(0)!!.MessageString
                        )
                    } else {
                        CustomToast.showToast(
                            this@CheckListForHousekeeping,
                            response.body()?.get(0)!!.MessageString
                        )
                    }

                } else {
                    CustomToast.showToast(
                        this@CheckListForHousekeeping,
                        "Response not successful"
                    )
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
                CustomToast.showToast(
                    this@CheckListForHousekeeping,
                    "Response failed"
                )
            }
        })
    }*/

    private fun getCheckList(clientCode: String, tourAutoId: String, headerAutoId: String) {
        retrofitInstance.getChecklistName("sams", clientCode, tourAutoId, headerAutoId)
            .enqueue(object : Callback<CheckListModel?> {
                override fun onResponse(
                    call: Call<CheckListModel?>,
                    response: Response<CheckListModel?>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { checkListAdapter.updateList(it) }
                    } else {

                        CustomToast.showToast(this@CheckListForHousekeeping, "No Checklist Found")
                    startActivity(Intent(this@CheckListForHousekeeping,MainActivity::class.java))

                    }
                }

                override fun onFailure(call: Call<CheckListModel?>, t: Throwable) {

                }
            })
    }
}