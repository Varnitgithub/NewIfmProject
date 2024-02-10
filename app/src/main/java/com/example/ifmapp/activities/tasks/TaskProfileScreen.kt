package com.example.ifmapp.activities.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.tasks.taskapi_response.TaskApiResponse
import com.example.ifmapp.activities.tasks.taskapi_response.TaskApiResponseItem
import com.example.ifmapp.activities.tasks.taskfragments.PreviousTaskFragment
import com.example.ifmapp.activities.tasks.taskfragments.TodoTaskFragment
import com.example.ifmapp.activities.tasks.taskfragments.UpComingTaskFragment
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityTaskProfileScreenBinding
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.GlobalLocation
import com.example.ifmapp.utils.UserObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskProfileScreen : AppCompatActivity() {
    private lateinit var binding: ActivityTaskProfileScreenBinding
    private var empId: String? = null
    private var empName: String? = null
    private var pin: String? = null
    private var siteSelect: String? = null
    private lateinit var siteList: ArrayList<String>
    private lateinit var retrofitInstance: ApiInterface

    private lateinit var previousTaskList: ArrayList<TaskApiResponseItem>
    private lateinit var todoTaskList: ArrayList<TaskApiResponseItem>
    private lateinit var upComingTaskList: ArrayList<TaskApiResponseItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_task_profile_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_profile_screen)
        empId = intent.getStringExtra("empId")

        //Initialization
        retrofitInstance = RetrofitInstance.apiInstance
        previousTaskList = ArrayList()
        todoTaskList = ArrayList()
        upComingTaskList = ArrayList()
        siteList = ArrayList()

        Log.d("TAGGGGGGGG", "onCreate: task profile screen...............$empId")

        for (users in SaveUsersInSharedPreference.getList(this@TaskProfileScreen)) {
            if (users.empId == UserObject.userId) {
                binding.userId.text = users.empId
                binding.userName.text = users.userName
                empName = users.userName
                pin = users.pin
                binding.designation.text = users.designation
            }
        }
        getSitesFromServer(UserObject.locationAutoId,GlobalLocation.location.latitude
            ,GlobalLocation.location.longitude,UserObject.userId)

        binding.previousTask.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            if (previousTaskList.isNotEmpty()){
                makeButtonHighlighted(binding.previousTask)
                makeButtonNonHighlighted(binding.todoTask)
                makeButtonNonHighlighted(binding.upComingTask)
                addFragment(PreviousTaskFragment(siteSelect.toString(),previousTaskList))
            }
            binding.progressBar.visibility = View.GONE


        }
        binding.todoTask.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            if (todoTaskList.isNotEmpty()){
                makeButtonHighlighted(binding.todoTask)
                makeButtonNonHighlighted(binding.previousTask)
                makeButtonNonHighlighted(binding.upComingTask)
                addFragment(TodoTaskFragment(siteSelect.toString(),todoTaskList))
            }
            binding.progressBar.visibility = View.GONE


        }
        binding.upComingTask.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            if (upComingTaskList.isNotEmpty()){
                makeButtonHighlighted(binding.upComingTask)
                makeButtonNonHighlighted(binding.todoTask)
                makeButtonNonHighlighted(binding.previousTask)
                addFragment(UpComingTaskFragment(siteSelect.toString(),upComingTaskList))
            }
            binding.progressBar.visibility = View.GONE


        }
getTasksList()
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.VISIBLE

    }


    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_FrameLayout, fragment)
            .commit()

    }

    private fun makeButtonHighlighted(textview: TextView) {
        textview.setBackgroundResource(R.drawable.button_back)
        textview.setTextColor(resources.getColor(R.color.white))
    }

    private fun makeButtonNonHighlighted(textview: TextView) {
        textview.setBackgroundResource(R.drawable.task_buttons_back_white)
        textview.setTextColor(resources.getColor(R.color.taskButton))
    }


    override fun onBackPressed() {
        val intent = Intent(this@TaskProfileScreen, MainActivity::class.java)
        intent.putExtra("mPIN", pin)
        intent.putExtra("empName", empName)
        intent.putExtra("empId", empId)
        startActivity(intent)
        super.onBackPressed()
    }


    fun getCurrentTimeFormatted(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun getFutureTimeFormatted(hoursToAdd: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, hoursToAdd)
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun timeToSeconds(time: String): Long {
        val parts = time.split(":")
        if (parts.size == 3) {
            val hours = parts[0].toLong()
            val minutes = parts[1].toLong()
            val seconds = parts[2].toLong()

            return hours * 3600 + minutes * 60 + seconds
        } else {
            return -1
        }
    }

    private fun getTasksList() {
        Log.d("TASSSSKKKK", "onResponse:...............task api called ")

        retrofitInstance.getTourTasks("sams").enqueue(object : Callback<TaskApiResponse?> {
            override fun onResponse(
                call: Call<TaskApiResponse?>,
                response: Response<TaskApiResponse?>
            ) {
                if (response.isSuccessful) {

                    var currentTime = getCurrentTimeFormatted()
                    var currentTimeInSeconds = timeToSeconds(currentTime)
                    var afterTime = getFutureTimeFormatted(2)
                    var afterTimeInSeconds = timeToSeconds(afterTime)

                    for (i in 0 until response.body()?.size!!) {
                        var fromTimeInSeconds = timeToSeconds(response.body()!![i].FromTime)
                        var toTimeInSeconds = timeToSeconds(response.body()!![i].ToTime)


                        if (fromTimeInSeconds < currentTimeInSeconds && toTimeInSeconds > currentTimeInSeconds) {

                            todoTaskList.add(response.body()!![i])


                        } else if (fromTimeInSeconds < currentTimeInSeconds) {
                            previousTaskList.add(response.body()!![i])


                        } else {
                            upComingTaskList.add(response.body()!![i])
                        }
                    }
                    addFragment(TodoTaskFragment(siteSelect.toString(),todoTaskList))
                    binding.progressBar.visibility = View.GONE

                    Log.d("TASSSSKKKK", "onResponse:todo.................${todoTaskList} ")
                    Log.d("TASSSSKKKK", "onResponse:previous.................${previousTaskList} ")
                    Log.d("TASSSSKKKK", "onResponse:upcoming.................${upComingTaskList} ")

                }
            }

            override fun onFailure(call: Call<TaskApiResponse?>, t: Throwable) {
                CustomToast.showToast(this@TaskProfileScreen, t.localizedMessage)
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
            this@TaskProfileScreen,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            ArrayList<String>()
        )

        for (site in SaveUsersInSharedPreference.getCurrentUserShifts(this@TaskProfileScreen, userId)) {
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