package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.activities.documents.DocumentImages
import com.example.ifmapp.adapters.DocumentsAdapter
import com.example.ifmapp.keys.Keys
import com.example.ifmapp.modelclasses.DocumentsModel

class MyDocumentsScreen : AppCompatActivity(), DocumentsAdapter.Clicked {
    private lateinit var documentsAdapter: DocumentsAdapter
    private var empName: String? = null
    private var pin: String? = null
    private var empId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_documents_screen)

        var documentsRecyclerView: RecyclerView = findViewById(R.id.documents_recyclerView)
        documentsAdapter = DocumentsAdapter(this, this)

        empName = intent.getStringExtra("empName")
        pin = intent.getStringExtra("mPIN")
        empId = intent.getStringExtra("empId")

        documentsRecyclerView.layoutManager = LinearLayoutManager(this)
        documentsRecyclerView.adapter = documentsAdapter

        val documentList = documentsList()
        documentsAdapter.updateList(documentList)
    }

    private fun documentsList(): ArrayList<DocumentsModel> {

        val documentList = arrayListOf<DocumentsModel>()

        documentList.add(DocumentsModel(1, R.drawable.aadhar, "Adhar Card", "","AdharCard"))
        documentList.add(DocumentsModel(1, R.drawable.pan, "Pan Card", " ","PanCard"))
        documentList.add(DocumentsModel(1, R.drawable.voterid, "Voter Id", " ","VoterId"))
        documentList.add(DocumentsModel(1, R.drawable.drivinglicense, "Driving License", " ","DrivingLicense"))
        return documentList
    }

    override fun onBackPressed() {
        var intent = Intent(this@MyDocumentsScreen, MainActivity::class.java)
        intent.putExtra("empName", empName)
        intent.putExtra("mPIN", pin)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun onclick(model: ArrayList<DocumentsModel>, position: Int) {
        val intent = Intent(this@MyDocumentsScreen, DocumentImages::class.java)
        intent.putExtra(Keys.ID_TYPE, model[position].docType)
        intent.putExtra("empId", empId)
    }
}