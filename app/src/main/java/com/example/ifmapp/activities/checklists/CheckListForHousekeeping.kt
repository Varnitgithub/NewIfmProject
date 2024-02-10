package com.example.ifmapp.activities.checklists

import CheckListAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.checklists.housekeeping_model.ViewPhotoResponse
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityCheckListForHousekeepingBinding
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
        empId = intent.getStringExtra("empId")
        siteSelect = intent.getStringExtra("siteSelect")
        tourCode = intent.getStringExtra("tourCode")
        mPosition = intent.getStringExtra("position")

        for (users in SaveUsersInSharedPreference.getList(this@CheckListForHousekeeping)) {
            if (users.empId == UserObject.userId) {
                empId = users.empId
                empName = users.userName
                pin = users.pin
            }
        }
        retrofitInstance = RetrofitInstance.apiInstance
        checkListAdapter = CheckListAdapter(this, this)
        binding.houseKeepingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.houseKeepingRecyclerView.adapter = checkListAdapter

        if (SaveUsersInSharedPreference.getHindiEnglish(this@CheckListForHousekeeping) == "hindi") {
            val checklist = checklistDataInHindi()
            checkListAdapter.updateList(checklist)
        } else {
            val checklist = checklistData()
            checkListAdapter.updateList(checklist)
        }
    }

    fun checklistData(): ArrayList<CheckListModel> {
        var checklist = ArrayList<CheckListModel>()

        checklist.add(
            CheckListModel(
                1, "Entrance glass clean & free\n" +
                        "from smudges", "pending", true
            )
        )
        checklist.add(
            CheckListModel(
                2, "Office floor are free of stairs\n" +
                        "& orderless", "pending", true
            )
        )
        checklist.add(CheckListModel(3, "Tables, printers are free from dust", "pending", true))
        checklist.add(CheckListModel(4, "The meeting room is clean and odorless", "pending", true))

        checklist.add(CheckListModel(5, "Bathroom is cleaned and odorless", "pending", true))

        checklist.add(
            CheckListModel(
                6,
                "The batihroom is stocked with soap, paper towels and toilet tissue",
                "pending",
                true
            )
        )
        checklist.add(
            CheckListModel(
                7,
                "Sufficient material available at the site",
                "pending",
                true
            )
        )


        return checklist
    }

    fun checklistDataInHindi(): ArrayList<CheckListModel> {
        var checklist = ArrayList<CheckListModel>()

        checklist.add(
            CheckListModel(
                1, "प्रवेश द्वार का शीशा साफ़ और मुफ़्त\n" +
                        "धब्बे से", "लंबित", true
            )
        )
        checklist.add(
            CheckListModel(
                2, "कार्यालय के फर्श पर सीढ़ियाँ नहीं हैं\n" +
                        "और व्यवस्थित", "लंबित", true
            )
        )
        checklist.add(CheckListModel(3, "टेबल, प्रिंटर धूल से मुक्त हैं", "लंबित", true))
        checklist.add(CheckListModel(4, "बैठक कक्ष साफ और गंधहीन है", "लंबित", true))

        checklist.add(
            CheckListModel(
                5,
                "बाथरूम साफ और गंधहीन है", "लंबित", true
            )
        )

        checklist.add(
            CheckListModel(
                6,
                "बाटीरूम में साबुन, कागज़ के तौलिये और शौचालय के टिश्यू भरे हुए हैं",
                "लंबित",
                true
            )
        )
        checklist.add(
            CheckListModel(
                7,
                "साइट पर पर्याप्त सामग्री उपलब्ध हैं", "लंबित", true
            )
        )


        return checklist
    }

    override fun onAddPhotoClick(checkListModel: CheckListModel, position: Int) {
        if (checkCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }

    override fun onViewPhotoClick(checkListModel: CheckListModel, position: Int) {
        var intent= Intent(this@CheckListForHousekeeping,ViewHouseKeepingPhoto::class.java)
        intent.putExtra("tourCode", tourCode)
        intent.putExtra("siteSelect", siteSelect)
        intent.putExtra("position", mPosition)
        startActivity(intent)
    }

    override fun onSwitchOnClick(checkListModel: CheckListModel, position: Int) {
        makeStatusComplete()
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
        var currentTime = getCurrentTimeFormatted()
        val imageIntoBase64 = bitmapToBase64(base64Image)

        Log.d(
            "ADDPHOTO",
            "viewPhoto: TAGGG................1 ${UserObject.userId} 2 ${UserObject.userNames}" +
                    "3 ${UserObject.locationAutoId} 4 ${siteSelect.toString()} 5 ${currentTime} 6 ${tourCode.toString()} 7 $mPosition" +
                    "  9${mPosition.toString()}"
        )

        if (UserObject.userId.isNotEmpty() &&
            UserObject.userNames.isNotEmpty() &&
            mPosition != null &&
            imageIntoBase64.isNotEmpty() &&
            UserObject.locationAutoId.isNotEmpty() &&
            siteSelect.toString().isNotEmpty()
            && currentTime.isNotEmpty() &&
            tourCode.toString().isNotEmpty()
        ) {
            retrofitInstance.insertCheckListImageHouseKeeping(
                "sams",
                UserObject.userId,
                UserObject.userNames,
                mPosition.toString(),
                imageIntoBase64,
                UserObject.locationAutoId,
                siteSelect.toString(),
                currentTime,
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
                                response.body()?.get(0)?.MessageString.toString()
                            )
                            Log.d("TARRRRRRRRRRRR", "onResponse: response success")
                        } else {
                            CustomToast.showToast(
                                this@CheckListForHousekeeping,
                                response.body()?.get(0)?.MessageString.toString()
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

    private fun makeStatusComplete() {
        val currentTime = getCurrentTimeFormatted()

        Log.d(
            "STATUS",
            "viewPhoto: TAGGG................1 ${UserObject.userId} 2 ${UserObject.userNames}" +
                    "3 ${UserObject.locationAutoId} 4 ${siteSelect.toString()} 5 ${currentTime} 6 ${tourCode.toString()} 7 $mPosition"
        )


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
    }
}