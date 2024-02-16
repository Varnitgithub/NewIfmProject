package com.example.ifmapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.ifmapp.R
import com.example.ifmapp.activities.tasks.TaskProfileScreen
import com.example.ifmapp.databinding.FragmentMytaskBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.utils.UserObject


class MyTaskFragment : Fragment() {
    private lateinit var binding: FragmentMytaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mytask, container, false)

        val usersList = SaveUsersInSharedPreference.getList(requireContext())
        /*
                for (user in usersList){
                    if (user.pin==pin&&user.userName==username){
                        binding.userName.text = user.userName

                        binding.designation.text = user.designation
                    }
                }*/
        binding.englishRadioBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SaveUsersInSharedPreference.setHindiEnglish(requireContext(),"english")

              //  binding.hindiRadioBtn.isChecked = false
                binding.btnDone.setBackgroundResource(R.drawable.button_back)
                binding.btnDone.setTextColor(resources.getColor(R.color.white))
            }
        }
       /* binding.hindiRadioBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SaveUsersInSharedPreference.setHindiEnglish(requireContext(),"hindi")
                binding.englishRadioBtn.isChecked = false
                binding.btnDone.setBackgroundResource(R.drawable.button_back)
                binding.btnDone.setTextColor(resources.getColor(R.color.white))
            }
        }*/

        binding.btnDone.setOnClickListener {
            languageSelectionDone()
        }

        return binding.root
    }

    private fun languageSelectionDone() {

        if (binding.englishRadioBtn.isChecked) {
            var intent = Intent(requireContext(), TaskProfileScreen::class.java)
            intent.putExtra("empId", UserObject.userId)
            startActivity(intent)
        }
    }


}