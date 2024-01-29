package com.example.ifmapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.adapters.AddAccountAdapter
import com.example.ifmapp.checked
import com.example.ifmapp.databinding.ActivityDashBoardScreenBinding
import com.example.ifmapp.modelclasses.AddAccountModel
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
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
      //  mobileNumber = currentUser.mobileNumber

        otpTextView = findViewById(R.id.otp_view)




        addAccountAdapter = AddAccountAdapter(this, this)
        binding.accountsRecyclerView.layoutManager =
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, true)
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



    override fun onclick(employeeModel: UserListModel, position: Int) {
        binding.userName.text ="Hi, ${employeeModel.userName}"
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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@DashBoardScreen,RegistrationScreen::class.java))
    }
}



