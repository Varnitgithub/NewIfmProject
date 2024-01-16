package com.example.ifmapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.R
import com.example.ifmapp.adapters.SalaryAdapter
import com.example.ifmapp.modelclasses.SalaryModel

class SalaryScreen : AppCompatActivity() {
    private lateinit var salaryAdapter: SalaryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary_screen)

        var recyclerView:RecyclerView = findViewById(R.id.salary_RecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        salaryAdapter = SalaryAdapter(this)

        recyclerView.adapter = salaryAdapter

        var salarydata = getsalary()
        salaryAdapter.updateList(salarydata)
    }


    fun getsalary():ArrayList<SalaryModel>{

        var salaryModel = arrayListOf<SalaryModel>()

        salaryModel.add(SalaryModel(1,"Jan"))
        salaryModel.add(SalaryModel(1,"Feb"))
        salaryModel.add(SalaryModel(1,"Mar"))
        salaryModel.add(SalaryModel(1,"Apr"))
        salaryModel.add(SalaryModel(1,"May"))
        salaryModel.add(SalaryModel(1,"Jun"))
        salaryModel.add(SalaryModel(1,"July"))
        salaryModel.add(SalaryModel(1,"Aug"))
        salaryModel.add(SalaryModel(1,"Sept"))
        salaryModel.add(SalaryModel(1,"Oct"))
        salaryModel.add(SalaryModel(1,"Nov"))
        salaryModel.add(SalaryModel(1,"Dec"))


        return salaryModel
    }
}