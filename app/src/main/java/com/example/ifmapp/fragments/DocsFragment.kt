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


class DocsFragment : Fragment() {
  private lateinit var binding: FragmentDocsBinding
  private lateinit var documentsAdapter: DocumentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(inflater,R.layout.fragment_docs, container, false)
        documentsAdapter = DocumentsAdapter(requireActivity())

        binding.documentsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.documentsRecyclerView.adapter = documentsAdapter

        val documentList = documentsList()
        documentsAdapter.updateList(documentList)
        return binding.root
    }


    private fun documentsList():ArrayList<DocumentsModel>{

        val documentList = arrayListOf<DocumentsModel>()

        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1,R.drawable.aadhar,"Aadhar Card","1234 5678 1357 2468"))

        return documentList
    }

    }
