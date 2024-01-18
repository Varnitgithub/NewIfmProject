package com.example.ifmapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.activities.CheckOutScreen
import com.example.ifmapp.activities.LeaveScreen
import com.example.ifmapp.activities.ProfileScreen
import com.example.ifmapp.activities.SalaryScreen
import com.example.ifmapp.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
private lateinit var binding: FragmentMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_menu,container,false)


        binding.btmProfile.setOnClickListener {
            startActivity(Intent(requireContext(),ProfileScreen::class.java))
        }

        binding.salaryBtm.setOnClickListener {
            startActivity(Intent(requireContext(),SalaryScreen::class.java))
        }

        binding.mustersBtm.setOnClickListener{
addFragment(MustersFragment(requireContext()))
        }

        binding.myDocBtm.setOnClickListener {
           addFragment(DocsFragment())
        }

        binding.homeBtm.setOnClickListener {
            startActivity(Intent(requireContext(),CheckOutScreen::class.java))
        }

        binding.leavesBtm.setOnClickListener {
            startActivity(Intent(requireContext(),LeaveScreen::class.java))
        }

        return binding.root
    }
    private fun addFragment(fragment: Fragment) {
      requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.blur_CL, fragment)
            .commit()

    }

}