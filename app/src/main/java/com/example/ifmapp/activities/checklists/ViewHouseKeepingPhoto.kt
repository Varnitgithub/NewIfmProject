package com.example.ifmapp.activities.checklists

import ViewPhotoHouseKeepingAdapter
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.checklists.housekeeping_model.ViewPhotoResponse
import com.example.ifmapp.activities.checklists.housekeeping_model.ViewPhotoResponseItem
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.UserObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewHouseKeepingPhoto : AppCompatActivity() {
    private var siteSelect: String? = null
    private var tourCode: String? = null
    private var position: String? = null
    private var headerSelect: String? = null
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var viewPhotoRecyclerView: RecyclerView
    private lateinit var viewPhotoHouseKeepingAdapter: ViewPhotoHouseKeepingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_house_keeping_photo)
        retrofitInstance = RetrofitInstance.apiInstance
        siteSelect = intent.getStringExtra("siteSelect")
        tourCode = intent.getStringExtra("tourCode")
        position = intent.getStringExtra("photoPosition")
        headerSelect = intent.getStringExtra("headerSelect")
        Log.d("TAGGGGGGGGG", "onCreate: $position is the position")
        viewPhotoRecyclerView = findViewById(R.id.viewPhotoRecyclerView)
        viewPhotoHouseKeepingAdapter = ViewPhotoHouseKeepingAdapter(this@ViewHouseKeepingPhoto)
        viewPhotoRecyclerView.layoutManager = LinearLayoutManager(this@ViewHouseKeepingPhoto)

        viewPhotoRecyclerView.adapter = viewPhotoHouseKeepingAdapter

        viewPhoto()

    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getCurrentTimeFormatted(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun viewPhoto() {


        val currentTime = getCurrentTimeFormatted()

        Log.d(
            "VIEWPHOTO",
            "viewPhoto: TAGGG................1 ${UserObject.userId} 2 ${headerSelect}" +
                    "3 ${UserObject.locationAutoId} 4 ${siteSelect.toString()} 5 ${currentTime} 6 ${tourCode.toString()} 7 $position"
        )

        if (
            position != null &&
            headerSelect.toString().isNotEmpty() &&
            siteSelect.toString().isNotEmpty() &&
            tourCode.toString().isNotEmpty()
        ) {
            Log.d("TAGGGGGGGGGGG", "viewPhoto: enter here")

            retrofitInstance.getChecklistImageUpdatedHouseKeeping(
                "sams",
                siteSelect.toString(),
                tourCode.toString(),
                headerSelect.toString(),
                position.toString()

            ).enqueue(object : Callback<ViewPhotoResponse?> {
                override fun onResponse(
                    call: Call<ViewPhotoResponse?>,
                    response: Response<ViewPhotoResponse?>
                ) {
                    if (response.isSuccessful) {
                        val responseBodySize = response.body()?.size
                        if (responseBodySize!=null && responseBodySize>0){
                            val imagesList = ArrayList<ViewPhotoResponseItem>()
                            for (i in 0 until responseBodySize) {
                                if (response.body()?.get(i) != null) {
                                    imagesList.add(response.body()?.get(i)!!)
                                }
                            }

                            if (imagesList.get(0).ImageBase64String==null){
                                Log.d("TAGGGGGGGGGGGG", "onResponse:empty list $imagesList")

                                viewPhotoHouseKeepingAdapter.updateList(emptyList())

                            }else{
                                Log.d("TAGGGGGGGGGGGG", "onResponse:non empty list $imagesList")

                                viewPhotoHouseKeepingAdapter.updateList(imagesList)

                            }
                        }


                    } else {
                        CustomToast.showToast(
                            this@ViewHouseKeepingPhoto,
                            "Response not successful"
                        )
                    }
                }

                override fun onFailure(call: Call<ViewPhotoResponse?>, t: Throwable) {
                    CustomToast.showToast(
                        this@ViewHouseKeepingPhoto,
                        "Response failed"
                    )
                }
            })
        } else {
            CustomToast.showToast(this@ViewHouseKeepingPhoto, "Please Enter All Details")
        }

    }
}