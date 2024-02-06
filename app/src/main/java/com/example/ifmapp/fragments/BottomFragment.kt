package com.example.ifmapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.FragmentBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomFragment : BottomSheetDialogFragment() {

private lateinit var binding:FragmentBottomBinding

    companion object {
        fun newInstance(): BottomFragment {
            return BottomFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bottom,container,false)







        return binding.root
    }
}