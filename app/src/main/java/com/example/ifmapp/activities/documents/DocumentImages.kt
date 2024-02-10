package com.example.ifmapp.activities.documents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.MyDocumentsScreen
import com.example.ifmapp.activities.tasks.TaskModel
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.keys.Keys
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.GlobalLocation
import com.example.ifmapp.utils.UserObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DocumentImages : AppCompatActivity() {
    private var empName: String? = null
    private var pin: String? = null
    private var empId: String? = null
    private var locationAutoId: String? = null
    private var docType: String? = null
    private lateinit var retrofitInstance: ApiInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_images)
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
        Log.d("GETDOCU", "getImagesOfDocuments: ${UserObject.userId} ${UserObject.locationAutoId} $docType")
        if ( UserObject.userId.isNotEmpty()&& UserObject.locationAutoId.isNotEmpty()&& docType.toString().isNotEmpty()){
            retrofitInstance.getEmployeeDocs(
                "sams", UserObject.userId, UserObject.locationAutoId, docType.toString()
            ).enqueue(object : Callback<TaskModel?> {
                override fun onResponse(call: Call<TaskModel?>, response: Response<TaskModel?>) {
                    if (response.isSuccessful) {

                        Log.d("TAGGGGGGGGGGG", "onResponse: \"Documents fetching successful\"")
                    }else{
                        Log.d("TAGGGGGGGGGGG", "onResponse: \"Documents fetching not successful\"")


                    }
                }

                override fun onFailure(call: Call<TaskModel?>, t: Throwable) {
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