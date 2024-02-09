package com.example.ifmapp.activities.tasks.taskfragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifmapp.R
import com.example.ifmapp.activities.checklists.CheckListForHousekeeping
import com.example.ifmapp.activities.tasks.TaskModel
import com.example.ifmapp.activities.tasks.taskadapter.TasksAdapter
import com.example.ifmapp.activities.tasks.taskapi_response.TaskApiResponseItem
import com.example.ifmapp.databinding.FragmentPreviousTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PreviousTaskFragment(private var siteSelect:String,private var previousLists: ArrayList<TaskApiResponseItem>) : Fragment(),
    TasksAdapter.Clicked {
    private lateinit var binding: FragmentPreviousTaskBinding
    private lateinit var tasksAdapter: TasksAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_previous_task, container, false)
        tasksAdapter = TasksAdapter(this, R.layout.previous_fragment_item)

        binding.previousRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.previousRecyclerView.adapter = tasksAdapter
        var mList = ArrayList<TaskModel>()
        var currentDate = getFormattedDate()
        for (i in 0 until previousLists.size) {
            var count = 1
            mList.add(TaskModel(count, previousLists[i].TourDesc, currentDate, "Completed"))
            count++
        }
        tasksAdapter.updateList(mList)




        return binding.root
    }

    private fun getFormattedDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onclick(model: TaskModel, position: Int) {
        var intent = Intent(requireContext(), CheckListForHousekeeping::class.java)

        intent.putExtra("tourCode", previousLists[position].TourCode)
        intent.putExtra("siteSelect", siteSelect)
        Log.d("TAGGGGGGGGGG", "onclick:...................... ${previousLists[position].TourCode}")
        startActivity(intent)
    }


}