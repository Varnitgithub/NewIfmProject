package com.example.ifmapp.activities

import SalaryAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivitySalaryScreenBinding
import com.example.ifmapp.modelclasses.SalaryModel

class SalaryScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySalaryScreenBinding
    private lateinit var search: ImageView
    private lateinit var salaryData:ArrayList<SalaryModel>
    private lateinit var salaryAdapter: SalaryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_salary_screen)
        binding = DataBindingUtil.setContentView(this, (R.layout.activity_salary_screen))

        binding.edtSalarySearch.visibility = View.GONE
        binding.salaryTxt.visibility = View.VISIBLE


        val recyclerView: RecyclerView = findViewById(R.id.salary_RecyclerView)
        search = findViewById(R.id.search_icon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        salaryAdapter = SalaryAdapter(this)

        recyclerView.adapter = salaryAdapter



          salaryData = getSalary()
        salaryAdapter.updateList(salaryData)

/*
        binding.edtSalarySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(changeInText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                findingSlip(changeInText.toString().trim(),salaryData)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })*/

        search.setOnClickListener {
            if (binding.edtSalarySearch.text.toString().isNotEmpty()){
                binding.salaryTxt.visibility = View.VISIBLE
                binding.edtSalarySearch.visibility = View.GONE
                findingSlip(binding.edtSalarySearch.text.toString().trim(),salaryData)
                binding.edtSalarySearch.text.clear()

            }else{
                binding.salaryTxt.visibility = View.GONE
                binding.edtSalarySearch.visibility = View.VISIBLE
            }


        }


        }


    private fun getSalary(): ArrayList<SalaryModel> {

        var salaryModel = arrayListOf<SalaryModel>()

        salaryModel.add(SalaryModel(1, "Jan 2023"))
        salaryModel.add(SalaryModel(2, "Feb 2023"))
        salaryModel.add(SalaryModel(3, "Mar 2023"))
        salaryModel.add(SalaryModel(4, "Apr 2023"))
        salaryModel.add(SalaryModel(5, "May 2023"))
        salaryModel.add(SalaryModel(6, "Jun 2023"))
        salaryModel.add(SalaryModel(7, "Jul 2023"))
        salaryModel.add(SalaryModel(8, "Aug 2023"))
        salaryModel.add(SalaryModel(9, "Sep 2023"))
        salaryModel.add(SalaryModel(10, "Oct 2023"))
        salaryModel.add(SalaryModel(11, "Nov 2023"))
        salaryModel.add(SalaryModel(12, "Dec 2023"))


        return salaryModel
    }

    private fun findingSlip(inputText:String,salaryList: ArrayList<SalaryModel>) {
           val matchingUsers = salaryList.filter { it.salaryDate.contains(inputText,ignoreCase = true)}
        // Display the result
        if (matchingUsers.isNotEmpty()) {
            salaryAdapter.updateList(matchingUsers as ArrayList<SalaryModel>)
        } else {
            Toast.makeText(this, "There is no salary slip", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SalaryScreen,MainActivity::class.java))
        super.onBackPressed()
    }
}
