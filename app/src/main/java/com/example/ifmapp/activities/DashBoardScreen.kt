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
import com.otpview.OTPListener
import com.otpview.OTPTextView

class DashBoardScreen : AppCompatActivity() ,AddAccountAdapter.OnClickedInterface{
    private lateinit var binding: ActivityDashBoardScreenBinding
    private lateinit var employeePinDao: EmployeePinDao
    private lateinit var otpLiveData: MutableLiveData<String>
    var otp: String? = null
    private lateinit var otpTextView : OTPTextView

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
        otpTextView = findViewById(R.id.otp_view) as OTPTextView
        Log.d("TAGGGGGGGGG", "onCreate: $pin and $mobileNumber")
        /////////////////////////////////////////////////
        otpLiveData = MutableLiveData()
        addAccountAdapter = AddAccountAdapter(this, this)
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

//        binding.forgotPin.paintFlags = binding.forgotPin.paintFlags or Paint.UNDERLINE_TEXT_FLAG
//        val editTexts = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)
        otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                employeePinDao.getcurrentEmployeeDetails(otp)
                    .observe(this@DashBoardScreen) {
                        if (it != null) {
                            if ( it.MessageID.toInt() == 1){
                                Log.d("TAGGGGGG", "onTextChanged:it is not null")
                                val intent =
                                    Intent(this@DashBoardScreen, MainActivity::class.java)

                                intent.putExtra("mPIN", otpTextView.otp)
                                intent.putExtra("empNumber", empNumber)
                                startActivity(intent)
                            }
                            else {
                                Toast.makeText(
                                    this@DashBoardScreen,
                                    "You entered wrong pin!",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            }
                    }
            }
        }
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



