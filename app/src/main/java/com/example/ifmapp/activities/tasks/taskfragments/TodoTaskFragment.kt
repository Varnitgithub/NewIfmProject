package com.example.ifmapp.activities.tasks.taskfragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.SearchCheckList
import com.example.ifmapp.activities.checklists.CheckListForHousekeeping
import com.example.ifmapp.activities.tasks.TaskModel
import com.example.ifmapp.activities.tasks.taskadapter.TasksAdapter
import com.example.ifmapp.activities.tasks.taskapi_response.TaskApiResponseItem
import com.example.ifmapp.databinding.FragmentTodoTaskBinding
import com.example.ifmapp.toast.CustomToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TodoTaskFragment(private var siteSelect:String,private var todoLists:ArrayList<TaskApiResponseItem>) : Fragment(),
    TasksAdapter.Clicked {
    private lateinit var binding:FragmentTodoTaskBinding
    private lateinit var tasksAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_todo_task, container, false)
        tasksAdapter = TasksAdapter(this,R.layout.todo_fragment_item)

        binding.todoRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.todoRecyclerview.adapter = tasksAdapter
        var mList  = ArrayList<TaskModel>()
        var currentDate=  getFormattedDate()

        if (todoLists.isNotEmpty()) {


            for (i in 0 until todoLists.size) {
                var count = 1
                mList.add(TaskModel(count, todoLists[i].TourDesc.toString(), currentDate, "TODO"))
                count++
            }
            tasksAdapter.updateList(mList)
        }else{
            CustomToast.showToast(requireContext(),"No Checklist Found")
            tasksAdapter.updateList(emptyList())
            startActivity(Intent(requireContext(), MainActivity::class.java))

        }
   return binding.root
    }
    private fun getFormattedDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onclick(model: TaskModel, position: Int) {
        var intent = Intent(requireContext(), CheckListForHousekeeping::class.java)
        intent.putExtra("siteSelect", siteSelect)
        intent.putExtra("position", (position+1).toString())
        intent.putExtra("tourCode",model.id.toString())
        Log.d("TAGGGGGGGGGGG", "onCreate: 22222.......${model.id}")
        startActivity(intent)
    }


}