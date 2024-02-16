package com.example.ifmapp.activities.documents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.MyDocumentsScreen
import com.example.ifmapp.activities.tasks.TaskModel
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityDocumentImagesBinding
import com.example.ifmapp.keys.Keys
import com.example.ifmapp.modelclasses.document_images_model.DocumentImagesModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.GlobalLocation
import com.example.ifmapp.utils.UserObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
private const val IMAGE_URL = "https://ifm360.in/SAMS/EmployeeDocsGroupL/"
class DocumentImages : AppCompatActivity() {
    private lateinit var binding:ActivityDocumentImagesBinding
    private var empName: String? = null
    private var pin: String? = null
    private var empId: String? = null
    private var locationAutoId: String? = null
    private var docType: String? = null
    private lateinit var retrofitInstance: ApiInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_images)
        binding  = DataBindingUtil.setContentView(this@DocumentImages,R.layout.activity_document_images)


        empId = UserObject.userId
        docType = intent.getStringExtra(Keys.ID_TYPE)

        retrofitInstance = RetrofitInstance.apiInstance
        for (users in SaveUsersInSharedPreference.getList(this@DocumentImages)) {
            if (users.empId == UserObject.userId) {
                empName = users.userName
                pin = users.pin
                locationAutoId = users.LocationAutoId
            }
        }
        getImagesOfDocuments()
    }

    private fun getImagesOfDocuments() {
        Log.d("GETDOCUMENT", "getImagesOfDocuments: ${UserObject.userId} ${UserObject.locationAutoId} $docType")
        if ( UserObject.userId.isNotEmpty()&& UserObject.locationAutoId.isNotEmpty()&& docType.toString().isNotEmpty()){
            retrofitInstance.getEmployeeDocs(
                "sams", UserObject.userId, UserObject.locationAutoId, docType.toString()
            ).enqueue(object : Callback<DocumentImagesModel?> {
                override fun onResponse(call: Call<DocumentImagesModel?>, response: Response<DocumentImagesModel?>) {
                    if (response.isSuccessful) {
                        for (i in 0 until response.body()!!.size){
                            if (i==0){
                                Log.d("IMAGES", "onResponse:i=0  $IMAGE_URL+${response.body()?.get(i)?.DocLocation}")
                                Glide.with(this@DocumentImages)
                                    .load("$IMAGE_URL${response.body()?.get(i)?.DocLocation}").into(binding.frontImg)

                            }else if(i==1){
                                Log.d("IMAGES", "onResponse:i=1 $IMAGE_URL${response.body()?.get(i)?.DocLocation}")

                                Glide.with(this@DocumentImages)
                                    .load("$IMAGE_URL${response.body()?.get(i)?.DocLocation}").into(binding.backImg)

                            }else{
                                Log.d("IMAGES", "onResponse:i=2  $IMAGE_URL${response.body()?.get(i)?.DocLocation}")

                                Glide.with(this@DocumentImages)
                                    .load("$IMAGE_URL${response.body()?.get(i)?.DocLocation}").into(binding.thirdImg)

                            }
                        }
                    }else{
                        Log.d("TAGGGGGGGGGGG", "onResponse: \"Documents fetching Unsuccessful\"")
                    }
                }
                override fun onFailure(call: Call<DocumentImagesModel?>, t: Throwable) {
                    Log.d("TAGGGGGGGGGGG", "onFailure: \"Documents Fetching Failed\"")
                }
            })
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(this@DocumentImages,MainActivity::class.java))
        super.onBackPressed()
    }
}