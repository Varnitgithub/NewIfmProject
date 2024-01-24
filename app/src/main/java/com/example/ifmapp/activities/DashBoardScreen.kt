package com.example.ifmapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Paint
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.adapters.AddAccountAdapter
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.ActivityDashBoardScreenBinding
import com.example.ifmapp.fragments.DocsFragment
import com.example.ifmapp.fragments.HomeFragment
import com.example.ifmapp.fragments.MenuFragment
import com.example.ifmapp.fragments.MustersFragment
import com.example.ifmapp.modelclasses.AddAccountModel
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem
import com.example.ifmapp.services.LocationService

class DashBoardScreen : AppCompatActivity() ,AddAccountAdapter.OnClickedInterface{
    private lateinit var binding: ActivityDashBoardScreenBinding
    private lateinit var employeePinDao: EmployeePinDao
    private lateinit var otpLiveData: MutableLiveData<String>
    var otp: String? = null
    private lateinit var addAccountAdapter: AddAccountAdapter
    private var mobileNumber: String? = null
    private var empNumber: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board_screen)
        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()
        val mobileNumber = intent.getStringExtra("usermobileNumber")
        val pin = intent.getStringExtra("PIN")

        Log.d("TAGGGGGGGGG", "onCreate: $pin and $mobileNumber")
        /////////////////////////////////////////////////
otpLiveData = MutableLiveData()
        addAccountAdapter = AddAccountAdapter(this,this)
        binding.accountsRecyclerView.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, true)
        binding.accountsRecyclerView.adapter = addAccountAdapter
        val employeesList = ArrayList<AddAccountModel>()


        employeePinDao.getFirstEmployeeDetails().observe(this) { employeeDetails ->
            employeeDetails?.let {
                binding.userName.text = "Hi, ${it.EmpName}"
                empNumber = employeeDetails.EmpNumber
            }
        }

        employeePinDao.getAllEmployeeDetails().observe(this) {
            val list = ArrayList<LoginByPINResponseItem>()
            list.remove(it[0])
            addAccountAdapter.updateList(list)
        }


        otpLiveData.observe(this) {
            otp + it
            Log.d("TAGGGGGGGGGGG", "onCreateView: pinnnn $otp")
            if (otp?.length == 4) {

//
            }
        }







        binding.forgotPin.setOnClickListener {
            startActivity(Intent(this, GnereratePinCodeScreen::class.java))
        }

        binding.addAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationScreen::class.java))

        }

        binding.forgotPin.paintFlags = binding.forgotPin.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        val editTexts = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)


        for (i in 0 until editTexts.size) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                    editTexts[i].inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                   /* val currentEditText = editTexts[i]
                    val previousEditText = if (i > 0) editTexts[i - 1] else null

                    if (previousEditText != null && previousEditText.text.isEmpty()) {
                        // Clear the current EditText and prevent further input
                        currentEditText.text.clear()
                        currentEditText.isFocusableInTouchMode = false
                        currentEditText.isFocusable = false
                    } else {
                        // Allow input in the current EditText
                        currentEditText.isFocusableInTouchMode = true
                        currentEditText.isFocusable = true
*/
                        if (editTexts[3].text.toString().isNotEmpty()) {
                            editTexts[i].inputType =
                                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                            otp = binding.otp1.text.toString().trim() + binding.otp2.text.toString()
                                .trim() +
                                    binding.otp3.text.toString()
                                        .trim() + binding.otp4.text.toString()
                                .trim()



                            employeePinDao.getcurrentEmployeeDetails(otp.toString())
                                .observe(this@DashBoardScreen) {

                                    if (it != null && it.MessageID.toInt() == 1) {
                                        Log.d("TAGGGGGG", "onTextChanged:it is not null")
                                        val intent =
                                            Intent(this@DashBoardScreen, MainActivity::class.java)

                                        intent.putExtra("mPIN", otp)
                                        intent.putExtra("empNumber", empNumber)
                                        startActivity(intent)
                                        Log.d(
                                            "TAGGGGGGGGGG",
                                            "onCreateView: $otp is the otp from dashboard"
                                        )
                                        binding.otp1.text.clear()
                                        binding.otp2.text.clear()
                                        binding.otp3.text.clear()
                                        binding.otp4.text.clear()


                                    } else {
                                        Toast.makeText(
                                            this@DashBoardScreen,
                                            "You entered wrong pin!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.otp1.text.clear()
                                        binding.otp2.text.clear()
                                        binding.otp3.text.clear()
                                        binding.otp4.text.clear()
                                    }
                                }
                        }
                   // }
                    binding.otp1.requestFocus()

                    // Show the keyboard after setting focus
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.otp1, InputMethodManager.SHOW_IMPLICIT)
                }
                override fun afterTextChanged(s: Editable?) {

                }
            })

            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    // If backspace is pressed, move the focus to the previous EditText
                    if (i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                    true
                } else {
                    false
                }
            }
        }
        setupOtpEditTextListeners()
////////////////////////////////////////////////////////

    }
    private fun setupOtpEditTextListeners() {
        binding.otp1.addTextChangedListener(createOtpTextWatcher(binding.otp2))
        binding.otp2.addTextChangedListener(createOtpTextWatcher(binding.otp3))
        binding.otp3.addTextChangedListener(createOtpTextWatcher(binding.otp4))
        binding.otp4.addTextChangedListener(createOtpTextWatcher(null))
    }

    private fun createOtpTextWatcher(nextEditText: EditText?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.length == 1) {
                    nextEditText?.requestFocus()

                }
            }

            override fun afterTextChanged(s: Editable?) {}


        }
    }

    override fun onclick(employeeModel: LoginByPINResponseItem, position: Int) {
        Log.d("TAGGGGGGGGGGG", "onclick: current user = ${employeeModel.mobileNumber}")
    }
}



