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
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.utils.CustomGridAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MyTaskFragment(private var context: Context,private var pin:String,private var username:String) : Fragment() {
    private lateinit var binding: FragmentMustersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_musters, container, false)

        val usersList = SaveUsersInSharedPreference.getList(requireContext())

        for (user in usersList){
            if (user.pin==pin&&user.userName==username){
                binding.userName.text = user.userName

                binding.designation.text = user.designation
            }
        }



        return binding.root
    }




}