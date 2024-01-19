package com.example.ifmapp.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.GnereratePinCodeScreen
import com.example.ifmapp.activities.MobileRegisterScreen
import com.example.ifmapp.adapters.AddAccountAdapter
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePin
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.FragmentHomeBinding
import com.example.ifmapp.modelclasses.AddAccountModel
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment(private var context: Context) : Fragment() {
    private lateinit var employeePinDao: EmployeePinDao
    private lateinit var binding: FragmentHomeBinding
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var employeeDB: EmployeeDB
    private lateinit var otpLiveData:MutableLiveData<String>
    var otp:String? = null
    private lateinit var addAccountAdapter: AddAccountAdapter
    private var mobileNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        employeePinDao = EmployeeDB.getInstance(requireContext()).employeePinDao()
        retrofitInstance = RetrofitInstance.apiInstance
        employeeDB = EmployeeDB.getInstance(context)
        addAccountAdapter = AddAccountAdapter(requireContext())
otpLiveData = MutableLiveData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val userMobileNumber = arguments?.getString("MOBILE_NUMBER")
        val pin = arguments?.getString("PIN")
        Log.d("TAGGGGGGGG", "onCreateView:mobile number is $userMobileNumber and pin is $pin")
        binding.accountsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, true)
        binding.accountsRecyclerView.adapter = addAccountAdapter
        val employeesList = ArrayList<AddAccountModel>()

        otpLiveData.observe(requireActivity()){
            otp+it
            Log.d("TAGGGGGGGGGGG", "onCreateView: pinnnn $otp")
if (otp?.length==4){

    employeePinDao.getEmployeePIN(otp?:"").observe(requireActivity()){
        Log.d("TAGGGGGGGGGGG", "onCreateView: pinnnn ${it.PIN} and ${it.mobileNumber}")
    }
}
        }


if (userMobileNumber!=null&&pin!=null){

    retrofitInstance.loginByPIN("sams", userMobileNumber,pin).enqueue(object : Callback<LoginByPINResponse?> {
        override fun onResponse(
            call: Call<LoginByPINResponse?>,
            response: Response<LoginByPINResponse?>
        ) {
            if (response.isSuccessful){

                CoroutineScope(Dispatchers.IO).launch {
                    employeePinDao.insertEmployeePin(EmployeePin(1,mobileNumber,pin,
                        response.body()?.get(0)?.EmpName.toString()
                    ))
                }
                binding.userName.text = response.body()?.get(0)?.EmpName

                employeesList.add(AddAccountModel(1,R.drawable.account_user_profile,response.body()?.get(0)?.EmpName!!))
                addAccountAdapter.updateList(employeesList)

            }else{

            }
        }

        override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {

        }
    })
}

        /*employeePinDao.getEmployeePIN().observe(requireActivity()) {

            retrofitInstance.loginByPIN(
                "sams",
                it.mobileNumber.toString(),
                it.PIN.toString()
            ).enqueue(object : Callback<LoginByPINResponse?> {
                override fun onResponse(
                    call: Call<LoginByPINResponse?>,
                    response: Response<LoginByPINResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            employeePinDao.insertEmployeeDetails(response.body()!!)

                        }
                        Log.d(
                            "TAGGGGGGG",
                            "onResponse: ${response.body()?.get(0)?.EmpName}"
                        )
                        binding.userName.text = "Hi, ${response.body()?.get(0)?.EmpName}"
                        employeesList.add(AddAccountModel(1, R.drawable.account_user_profile, response.body()!![0].EmpName))
                        addAccountAdapter.updateList(employeesList)

                    } else {
                        Log.d("TAGGGGGGG", "onResponse:user is not login ")

                    }
                }

                override fun onFailure(
                    call: Call<LoginByPINResponse?>,
                    t: Throwable
                ) {
                    Log.d("TAGGGGGG", "onFailure: login failed")
                }
            })

        }*/







        binding.forgotPin.setOnClickListener {
            startActivity(Intent(requireActivity(), GnereratePinCodeScreen::class.java))
        }

        binding.addAccount.setOnClickListener {
            startActivity(Intent(requireActivity(), MobileRegisterScreen::class.java))

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
                    otpLiveData.postValue(editTexts[i].toString())
                    Log.d("TAGGGGGGGGGGGG", "onTextChanged: pin is ${editTexts[i]}")
                    if (editTexts[3].text.toString().isNotEmpty()) {

                        editTexts[i].inputType =
                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                        val tableName = "EmployeeDetailsTable"

                    }
                    editTexts[i].inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
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




        return binding.root


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


}