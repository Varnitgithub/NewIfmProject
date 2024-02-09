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
        empId = intent.getStringExtra("empId")
        docType = intent.getStringExtra(Keys.ID_TYPE)

        retrofitInstance = RetrofitInstance.apiInstance
        for (users in SaveUsersInSharedPreference.getList(this@DocumentImages)) {
            if (users.empId == empId) {
                empName = users.userName
                pin = users.pin
                locationAutoId = users.LocationAutoId
            }


        }
    }


    fun getImagesOfDocuments() {
        retrofitInstance.getEmployeeDocs(
            "sams", empId.toString(), locationAutoId.toString(), docType.toString()
        ).enqueue(object : Callback<TaskModel?> {
            override fun onResponse(call: Call<TaskModel?>, response: Response<TaskModel?>) {
                if (response.isSuccessful) {

                    Log.d("TAGGGGGGGGGGG", "onResponse: \"Documents fetching successful\"")
                }else{
                    Log.d("TAGGGGGGGGGGG", "onResponse: \"Documents fetching not successful\"")


                }
            }

            override fun onFailure(call: Call<TaskModel?>, t: Throwable) {
                Log.d("TAGGGGGGGGGGG", "onFailure: \"Documents fetching successful\"")


            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@DocumentImages, MyDocumentsScreen::class.java)
        intent.putExtra("mPIN", pin)
        intent.putExtra("empName", empName)
        intent.putExtra("empId", empId)
        startActivity(intent)
        super.onBackPressed()
    }
}