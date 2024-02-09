package com.example.ifmapp.activities.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.HouseKeepingChecklistScreen
import com.example.ifmapp.activities.checklists.CheckListForHousekeeping
import com.example.ifmapp.activities.tasks.taskapi_response.TaskApiResponse
import com.example.ifmapp.activities.tasks.taskapi_response.TaskApiResponseItem
import com.example.ifmapp.activities.tasks.taskfragments.PreviousTaskFragment
import com.example.ifmapp.activities.tasks.taskfragments.TodoTaskFragment
import com.example.ifmapp.activities.tasks.taskfragments.UpComingTaskFragment
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityTaskProfileScreenBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskProfileScreen : AppCompatActivity() {
    private lateinit var binding: ActivityTaskProfileScreenBinding
    private var empId: String? = null
    private var empName: String? = null
    private var pin: String? = null
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

        Log.d("TAGGGGGGGG", "onCreate: task profile screen...............$empId")

        for (users in SaveUsersInSharedPreference.getList(this@TaskProfileScreen)) {
            if (users.empId == empId) {
                binding.userId.text = users.empId
                binding.userName.text = users.userName
                empName = users.userName
                pin = users.pin
                binding.designation.text = users.designation
            }
        }

        binding.previousTask.setOnClickListener {
            if (previousTaskList.isNotEmpty()){
                makeButtonHighlighted(binding.previousTask)
                makeButtonNonHighlighted(binding.todoTask)
                makeButtonNonHighlighted(binding.upComingTask)
                addFragment(PreviousTaskFragment(previousTaskList))
            }

        }
        binding.todoTask.setOnClickListener {
            if (todoTaskList.isNotEmpty()){
                makeButtonHighlighted(binding.todoTask)
                makeButtonNonHighlighted(binding.previousTask)
                makeButtonNonHighlighted(binding.upComingTask)
                addFragment(TodoTaskFragment(todoTaskList))
            }

        }
        binding.upComingTask.setOnClickListener {
            if (upComingTaskList.isNotEmpty()){
                makeButtonHighlighted(binding.upComingTask)
                makeButtonNonHighlighted(binding.todoTask)
                makeButtonNonHighlighted(binding.previousTask)
                addFragment(UpComingTaskFragment(upComingTaskList))
            }

        }
getTasksList()
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


                        } else if (fromTimeInSeconds > currentTimeInSeconds) {
                            previousTaskList.add(response.body()!![i])


                        } else {
                            upComingTaskList.add(response.body()!![i])
                        }
                    }
                    addFragment(TodoTaskFragment(todoTaskList))
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

}