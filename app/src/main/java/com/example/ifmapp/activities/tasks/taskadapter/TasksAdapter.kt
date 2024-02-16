package com.example.ifmapp.activities.tasks.taskadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ifmapp.R
import com.example.ifmapp.activities.tasks.TaskModel
import com.example.ifmapp.modelclasses.DocumentsModel


class TasksAdapter(
    private var listener: Clicked,
    var itemView: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var taskList = ArrayList<TaskModel>()

    // Modify the adapter constructor to accept a click listener
    fun setClickListener(clickedListener: Clicked) {
        this.listener = clickedListener
    }

    inner class PreviousTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var previousCL: ConstraintLayout = itemView.findViewById(R.id.previous_CL)
        var previousDate: TextView = itemView.findViewById(R.id.previousTaskDate)
        var previousTaskNo: TextView = itemView.findViewById(R.id.previousTaskNo)
        var previousStatus: TextView = itemView.findViewById(R.id.previousTaskStatus)

        init {
            previousCL.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = taskList[position]
                    listener.onclick(clickedItem, position)
                }
            }
        }
    }

    inner class TodoTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var todoCL: ConstraintLayout = itemView.findViewById(R.id.todo_CL)
        var todoDate: TextView = itemView.findViewById(R.id.todoDate)
        var todoTaskNo: TextView = itemView.findViewById(R.id.todoTaskNo)
        var todoStatus: TextView = itemView.findViewById(R.id.todoStatus)

        init {
            todoCL.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = taskList[position]
                    listener.onclick(clickedItem, position)
                }
            }
        }
    }

    inner class UpcomingTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var upComingCL: ConstraintLayout = itemView.findViewById(R.id.upComing_CL)
        var upComingTaskDate: TextView = itemView.findViewById(R.id.upComingTaskDate)
        var upComingTaskNo: TextView = itemView.findViewById(R.id.upcoming_taskno)
        var upComingTaskStatus: TextView = itemView.findViewById(R.id.upComingTaskStatus)

        init {

            upComingCL.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = taskList[position]
                    listener.onclick(clickedItem, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemView, parent, false)

        return when (itemView) {
            R.layout.previous_fragment_item -> PreviousTaskViewHolder(view)
            R.layout.todo_fragment_item -> TodoTaskViewHolder(view)
            R.layout.upcoming_fragment_item -> UpcomingTaskViewHolder(view)
            else -> throw IllegalArgumentException("Unknown layout ID: $itemView")
        }
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = taskList[position]

        when (holder) {
            is PreviousTaskViewHolder -> {
                holder.previousTaskNo.text = currentItem.taskName
                holder.previousDate.text = currentItem.date
                holder.previousStatus.text = currentItem.status
            }

            is TodoTaskViewHolder -> {
                holder.todoTaskNo.text = currentItem.taskName
                holder.todoDate.text = currentItem.date
                holder.todoStatus.text = currentItem.status
            }

            is UpcomingTaskViewHolder -> {
                holder.upComingTaskNo.text = currentItem.taskName
                holder.upComingTaskDate.text = currentItem.date
                holder.upComingTaskStatus.text = currentItem.status
            }
        }
    }
    fun updateList(newList: List<TaskModel>) {
        taskList.clear()
        taskList.addAll(newList)

      notifyDataSetChanged()
    }

    interface Clicked {
        fun onclick(model: TaskModel, position: Int)
    }
}
