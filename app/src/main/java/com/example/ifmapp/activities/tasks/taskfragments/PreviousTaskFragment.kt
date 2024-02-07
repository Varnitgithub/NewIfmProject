package com.example.ifmapp.activities.tasks.taskfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.FragmentPreviousTaskBinding

class PreviousTaskFragment : Fragment() {
  private lateinit var binding:FragmentPreviousTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_previous_task, container, false)

    return binding.root
    }


}