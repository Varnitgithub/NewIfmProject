package com.example.ifmapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.activities.MyDocumentsScreen
import com.example.ifmapp.R
import com.example.ifmapp.activities.LeaveScreen
import com.example.ifmapp.activities.ProfileScreen
import com.example.ifmapp.activities.SalaryScreen
import com.example.ifmapp.activities.TaskScreen
import com.example.ifmapp.databinding.FragmentMenuBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.utils.UserObject

class MenuFragment(private var pin:String,private var userName:String,private var empNumber:String) : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private var username:String?=null
    private var designation:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false)

        val usersList = SaveUsersInSharedPreference.getList(requireContext())

        for (user in usersList){
            if (user.pin==UserObject.userPin&&user.userName==UserObject.userNames){
                username = user.userName
                designation = user.designation
            }
        }

        binding.btmProfile.setOnClickListener {
           val intent = Intent(requireContext(), ProfileScreen::class.java)
            intent.putExtra("mPIN",pin)
            intent.putExtra("empName",username)
            startActivity(intent)
        }

        binding.salaryBtm.setOnClickListener {
           val intent = Intent(requireContext(), SalaryScreen::class.java)
            intent.putExtra("mPIN",pin)
            intent.putExtra("empName",username)

            startActivity(intent)
        }

        binding.musters.setOnClickListener {

          val intent =   Intent(requireContext(),TaskScreen::class.java)
            intent.putExtra("mPIN",pin)
            intent.putExtra("empName",username)
            startActivity(intent)
        }

        binding.mydoc.setOnClickListener {
            val intent =   Intent(requireContext(), MyDocumentsScreen::class.java)
            intent.putExtra("mPIN",pin)
            intent.putExtra("empName",username)
            intent.putExtra("empId",empNumber)
            startActivity(intent)
        }



        binding.leavesBtm.setOnClickListener {
            val intent = Intent(requireContext(), LeaveScreen::class.java)
            intent.putExtra("mPIN",pin)
            intent.putExtra("empName",username)
            startActivity(intent)
        }



        return binding.root
    }

    private fun addFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.blur_CL, fragment)
            .commit()

    }



}