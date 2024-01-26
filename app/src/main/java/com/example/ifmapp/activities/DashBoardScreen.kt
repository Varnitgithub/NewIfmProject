package com.example.ifmapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Paint
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
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
import com.example.ifmapp.checked
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.ActivityDashBoardScreenBinding
import com.example.ifmapp.fragments.DocsFragment
import com.example.ifmapp.fragments.HomeFragment
import com.example.ifmapp.fragments.MenuFragment
import com.example.ifmapp.fragments.MustersFragment
import com.example.ifmapp.modelclasses.AddAccountModel
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.services.LocationService
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.otpview.OTPListener
import com.otpview.OTPTextView

class DashBoardScreen : AppCompatActivity(), AddAccountAdapter.OnClickedInterface {
    private lateinit var binding: ActivityDashBoardScreenBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 111
    private lateinit var otpTextView: OTPTextView

    private lateinit var addAccountAdapter: AddAccountAdapter


    private var mobileNumber: String? = null
    private var empNumber: String? = null
    private var empName: String? = null
    var empPin: String? = null
    var currentEmployee: UserListModel? = null
    private lateinit var currentUser: UserListModel
    private lateinit var allUsersList: ArrayList<UserListModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board_screen)

        currentUser = SaveUsersInSharedPreference.getList(this)[0]

        binding.userName.text = "Hi, ${currentUser.userName}"
        allUsersList = ArrayList()
        empNumber = currentUser.empId
        mobileNumber = currentUser.mobileNumber

        otpTextView = findViewById(R.id.otp_view)


        addAccountAdapter = AddAccountAdapter(this, this)
        binding.accountsRecyclerView.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, true)
        binding.accountsRecyclerView.adapter = addAccountAdapter
        val employeesList = ArrayList<AddAccountModel>()


        val newlist = SaveUsersInSharedPreference.getList(this).size
        for (user in 1 until newlist) {
            allUsersList.add(SaveUsersInSharedPreference.getList(this)[user])
        }

        addAccountAdapter.updateList(allUsersList)

        binding.forgotPin.setOnClickListener {
            startActivity(Intent(this, GnereratePinCodeScreen::class.java))
        }

        binding.addAccount.setOnClickListener {
            checked.isChecked = true
            startActivity(Intent(this, RegistrationScreen::class.java))

        }

        otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {

                val intent = Intent(this@DashBoardScreen, MainActivity::class.java)
                intent.putExtra("mPIN", otp)
                intent.putExtra("mobileNumber", mobileNumber)
                intent.putExtra("empId", empNumber)
                startActivity(intent)

            }
        }
    }

    override fun onStop() {
        super.onStop()
        otpTextView.setOTP("")
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

    override fun onclick(employeeModel: UserListModel, position: Int) {
        Log.d("TAGGGGGGGGGGG", "onclick: current user = ${employeeModel.mobileNumber}")
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermission()
            }
            //Permission Granted


        }
    }
}



