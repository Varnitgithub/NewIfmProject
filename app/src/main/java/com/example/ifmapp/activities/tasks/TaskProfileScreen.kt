package com.example.ifmapp.activities.tasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ifmapp.R
import com.example.ifmapp.activities.tasks.taskfragments.PreviousTaskFragment
import com.example.ifmapp.activities.tasks.taskfragments.TodoTaskFragment
import com.example.ifmapp.activities.tasks.taskfragments.UpComingTaskFragment
import com.example.ifmapp.databinding.ActivityTaskProfileScreenBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference

class TaskProfileScreen : AppCompatActivity() {
    private lateinit var binding: ActivityTaskProfileScreenBinding
    private var empId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_task_profile_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_profile_screen)
empId = intent.getStringExtra("empId")


        for (users in SaveUsersInSharedPreference.getList(this@TaskProfileScreen)){
            if (users.empId==empId){
                binding.userId.text = users.empId
                binding.userName.text = users.userName
                binding.designation.text = users.designation
            }
        }

        binding.previousTask.setOnClickListener {
            makeButtonHighlighted(binding.previousTask)
            makeButtonNonHighlighted(binding.todoTask)
            makeButtonNonHighlighted(binding.upComingTask)
           // addFragment(PreviousTaskFragment())
        }
        binding.todoTask.setOnClickListener {
            makeButtonHighlighted(binding.todoTask)
            makeButtonNonHighlighted(binding.previousTask)
            makeButtonNonHighlighted(binding.upComingTask)
          //  addFragment(TodoTaskFragment())
        }
        binding.upComingTask.setOnClickListener {
            makeButtonHighlighted(binding.upComingTask)
            makeButtonNonHighlighted(binding.todoTask)
            makeButtonNonHighlighted(binding.previousTask)
         //   addFragment(UpComingTaskFragment())
        }

    }


    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
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

    fun getSpinnerList(){

    }
}