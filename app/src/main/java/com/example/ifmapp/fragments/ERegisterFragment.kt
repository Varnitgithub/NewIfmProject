package com.example.ifmapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifmapp.R
import com.example.ifmapp.adapters.DocumentsAdapter
import com.example.ifmapp.databinding.FragmentDocsBinding
import com.example.ifmapp.modelclasses.DocumentsModel


class ERegisterFragment(private var pin:String) : Fragment() {
  private lateinit var binding: FragmentDocsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(inflater,R.layout.fragment_docs, container, false)


        return binding.root
    }




    }
