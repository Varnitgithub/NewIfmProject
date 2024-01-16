package com.example.ifmapp.activities

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.R
import com.example.ifmapp.adapters.SalaryAdapter
import com.example.ifmapp.databinding.ActivitySalaryScreenBinding
import com.example.ifmapp.modelclasses.SalaryModel
import java.util.Locale

class SalaryScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySalaryScreenBinding
    private lateinit var search: ImageView
    private lateinit var salaryAdapter: SalaryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary_screen)
        binding = DataBindingUtil.setContentView(this, (R.layout.activity_salary_screen))

        binding.edtSalarySearchCL.visibility = View.GONE
        binding.topIconsCL.visibility = View.VISIBLE


        val recyclerView: RecyclerView = findViewById(R.id.salary_RecyclerView)
        search = findViewById(R.id.search_icon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        salaryAdapter = SalaryAdapter(this)

        recyclerView.adapter = salaryAdapter

        val salarydata = getSalary()
        salaryAdapter.updateList(salarydata)

        search.setOnClickListener {
            binding.topIconsCL.visibility = View.GONE
            binding.edtSalarySearchCL.visibility = View.VISIBLE
            val salaryData = getSalary()

           binding.edtSalarySearch.addTextChangedListener(object : TextWatcher {
               override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

               }

               override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                   findingSlip(p0.toString().trim(),salaryData)
               }

               override fun afterTextChanged(p0: Editable?) {
               }
           })

        }
    }


    private fun getSalary(): ArrayList<SalaryModel> {

        var salaryModel = arrayListOf<SalaryModel>()

        salaryModel.add(SalaryModel(1, "Jan 2023"))
        salaryModel.add(SalaryModel(1, "Feb 2023"))
        salaryModel.add(SalaryModel(1, "Mar 2023"))
        salaryModel.add(SalaryModel(1, "Apr 2023"))
        salaryModel.add(SalaryModel(1, "May 2023"))
        salaryModel.add(SalaryModel(1, "Jun 2023"))
        salaryModel.add(SalaryModel(1, "Jul 2023"))
        salaryModel.add(SalaryModel(1, "Aug 2023"))
        salaryModel.add(SalaryModel(1, "Sept 2023"))
        salaryModel.add(SalaryModel(1, "Oct 2023"))
        salaryModel.add(SalaryModel(1, "Nov 2023"))
        salaryModel.add(SalaryModel(1, "Dec 2023"))


        return salaryModel
    }

    private fun findingSlip(inputText:String,salaryList: ArrayList<SalaryModel>) {
           val matchingUsers = salaryList.filter { it.salaryDate.contains(inputText,ignoreCase = true)}
        // Display the result
        if (matchingUsers.isNotEmpty()) {
            Toast.makeText(this, "$matchingUsers is the user", Toast.LENGTH_SHORT).show()
            salaryAdapter.updateList(matchingUsers as ArrayList<SalaryModel>)
        } else {
            Toast.makeText(this, "There is no salary slip", Toast.LENGTH_SHORT).show()
        }
    }
}
