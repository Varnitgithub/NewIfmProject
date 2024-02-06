package com.example.ifmapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.adapters.DocumentsAdapter
import com.example.ifmapp.modelclasses.DocumentsModel

class MyDocumentsScreen : AppCompatActivity() {
    private lateinit var documentsAdapter: DocumentsAdapter
    private var empName:String?=null
    private var pin:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_documents_screen)

        var documentsRecyclerView: RecyclerView = findViewById(R.id.documents_recyclerView)
        documentsAdapter = DocumentsAdapter(this)

        empName  = intent.getStringExtra("empName")
        pin = intent.getStringExtra("mPIN")

        documentsRecyclerView.layoutManager = LinearLayoutManager(this)
        documentsRecyclerView.adapter = documentsAdapter

        val documentList = documentsList()
        documentsAdapter.updateList(documentList)
    }

    private fun documentsList(): ArrayList<DocumentsModel> {

        val documentList = arrayListOf<DocumentsModel>()

        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Aadhar Card", "1234 5678 1357 2468"))
        return documentList
    }

    override fun onBackPressed() {
        var intent = Intent(this@MyDocumentsScreen,MainActivity::class.java)
        intent.putExtra("empName",empName)
        intent.putExtra("mPIN",pin)
        startActivity(intent)
        super.onBackPressed()
    }
}