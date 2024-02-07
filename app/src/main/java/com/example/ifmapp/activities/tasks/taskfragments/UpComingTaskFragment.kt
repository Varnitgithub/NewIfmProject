package com.example.ifmapp.activities.tasks.taskfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.FragmentUpComingTaskBinding

class UpComingTaskFragment : Fragment() {
 private lateinit var binding:FragmentUpComingTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_up_coming_task, container, false)









    return binding.root
    }


    }
