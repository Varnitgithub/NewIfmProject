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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ifmapp.MainActivity
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
import com.example.ifmapp.databinding.MainActivityBinding
import com.example.ifmapp.modelclasses.AddAccountModel
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem
import com.example.ifmapp.shared_preference.MyPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment(private var context: Context) : Fragment(),
    AddAccountAdapter.OnClickedInterface {
    private lateinit var employeePinDao: EmployeePinDao
    private lateinit var binding: FragmentHomeBinding
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var employeeDB: EmployeeDB
    private lateinit var otpLiveData: MutableLiveData<String>
    var otp: String? = null
    private lateinit var addAccountAdapter: AddAccountAdapter
    private var mobileNumber: String? = null
    private lateinit var myPreferences: MyPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        employeePinDao = EmployeeDB.getInstance(requireContext()).employeePinDao()
        retrofitInstance = RetrofitInstance.apiInstance
        employeeDB = EmployeeDB.getInstance(context)
        addAccountAdapter = AddAccountAdapter(requireContext(), this)
        otpLiveData = MutableLiveData()
        myPreferences = MyPreferences(requireContext())
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


        employeePinDao.getFirstEmployeeDetails().observe(requireActivity()) { employeeDetails ->
            employeeDetails?.let {
                binding.userName.text = it.EmpName
            }
        }

        employeePinDao.getAllEmployeeDetails().observe(requireActivity()) {
            addAccountAdapter.updateList(it)
        }


        otpLiveData.observe(requireActivity()) {
            otp + it
            Log.d("TAGGGGGGGGGGG", "onCreateView: pinnnn $otp")
            if (otp?.length == 4) {

//
            }
        }







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
                    if (editTexts[3].text.toString().isNotEmpty()) {
                        editTexts[i].inputType =
                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                        otp = binding.otp1.text.toString().trim() + binding.otp2.text.toString()
                            .trim() +
                                binding.otp3.text.toString().trim() + binding.otp4.text.toString()
                            .trim()
                    }
                    editTexts[i].inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

                employeePinDao.getcurrentEmployeeDetails(otp.toString()).observe(requireActivity()){
                    if (it!=null){
                        Log.d("TAGGGGGG", "onTextChanged:it is not null")

                        val intent = Intent(requireContext(),MainActivity::class.java)

                        intent.putExtra("mPIN",otp)
                        startActivity(intent)

                    }else{
                        Toast.makeText(requireContext(), "this is invalid mPIN, Please enter valid pin", Toast.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(), "this is invalid mPIN, Please enter valid pin", Toast.LENGTH_SHORT).show()
                    }
                }



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

    override fun onclick(employeeModel: LoginByPINResponseItem, position: Int) {
        Log.d("TAGGGGGGGGGGG", "onclick: current user = ${employeeModel.mobileNumber}")
    }


}