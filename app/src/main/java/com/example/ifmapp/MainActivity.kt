package com.example.ifmapp

import android.content.Intent
import com.example.ifmapp.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.activities.CheckInScreen
import com.example.ifmapp.activities.ScannerScreen
import com.example.ifmapp.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.main_activity)


        binding.checkInBtn.setOnClickListener {
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_back)
            startActivity(Intent(this,ScannerScreen::class.java))
        }

        binding.checkoutBtn.setOnClickListener {
            binding.checkoutBtn.isEnabled = false
            binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_back)

            startActivity(Intent(this,ScannerScreen::class.java))

        }
// Define the items for the dropdown
        val siteItems = arrayOf("IL Environmental Infrastructure Services Limited",
            "Big Kahuna Burger Ltd.",
            "FS Environmental Infrastructure Services Limited")
  val shiftItems = arrayOf("Shift A              8:52",
            "Shift B              8:52",
            "Shift C              8:52",
      "Shift D              8:52")


        val adapterSelectionSite = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, siteItems)
        val adapterSelectionShift = ArrayAdapter(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item, shiftItems)
        adapterSelectionSite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterSelectionShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectSite.adapter = adapterSelectionSite
        binding.spinnerSelectShift.adapter = adapterSelectionShift

        // Set item selected listener for the spinner
        binding.spinnerSelectSite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle the selected item
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, "you select $selectedItem", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing here
            }
        }

        binding.spinnerSelectShift.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle the selected item
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, "you select $selectedItem", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing here
            }
        }

    }
}
