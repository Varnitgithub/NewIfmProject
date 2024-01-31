package com.example.ifmapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.ifmapp.R
import com.example.ifmapp.databinding.FragmentMustersBinding
import com.example.ifmapp.utils.CustomGridAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MyTaskFragment(private var context: Context,private var pin:String) : Fragment() {
    private lateinit var binding: FragmentMustersBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_musters, container, false)



        return binding.root
    }




}