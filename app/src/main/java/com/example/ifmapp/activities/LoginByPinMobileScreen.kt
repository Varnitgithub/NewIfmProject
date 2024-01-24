package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.ActivityLoginByPinMobileScreenBinding
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginByPinMobileScreen : AppCompatActivity() {
    private lateinit var employeePinDao: EmployeePinDao
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var binding: ActivityLoginByPinMobileScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_by_pin_mobile_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_by_pin_mobile_screen)
        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()
        retrofitInstance = RetrofitInstance.apiInstance

        binding.btnContinue.setOnClickListener {
            loginUser()
        }

        binding.employeepinEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
             if (binding.employeeMobileEdt.text.toString().length==10&&s?.length==4){

                 binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                 binding.btnContinue.setBackgroundResource(R.drawable.button_back)


             }else{
                 binding.btnContinue.setTextColor(resources.getColor(R.color.check_btn))
                 binding.btnContinue.setBackgroundResource(R.drawable.button_backwhite)
             }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }


    private fun loginUser() {

        if (binding.employeeMobileEdt.text != null && binding.employeepinEdt.text != null) {

            if (binding.employeeMobileEdt.text.toString().length <= 10 && binding.employeepinEdt.text.toString().length <= 4) {

                retrofitInstance.loginByPIN(
                    "sams",
                    binding.employeeMobileEdt.text.toString().trim(),
                    binding.employeepinEdt.text.toString()
                ).enqueue(object : Callback<LoginByPINResponse?> {
                    override fun onResponse(
                        call: Call<LoginByPINResponse?>,
                        response: Response<LoginByPINResponse?>
                    ) {
                        if (response.isSuccessful&& response.body()?.get(0)?.MessageID?.toInt()==1) {
                            Log.d(
                                "TAGGGGGGGGGGG",
                                "onResponse: response getting successfully in login mob screen"
                            )

                            val emp: LoginByPINResponseItem? = response.body()?.get(0)
                            val loginByPINResponseItem =
                                LoginByPINResponseItem(
                                    Designation = emp?.Designation ?: "",
                                    EmpName = emp?.EmpName ?: "",
                                    EmpNumber = emp?.EmpNumber ?: "",
                                    LocationAutoID = emp?.LocationAutoID ?: "",
                                    MessageID = emp?.MessageID ?: "",
                                    MessageString = emp?.MessageString ?: "",
                                    pin = binding.employeepinEdt.text.toString().trim(),
                                    mobileNumber = binding.employeeMobileEdt.text.toString().trim()
                                )
                            CoroutineScope(Dispatchers.IO).launch {
                                employeePinDao.insertEmployeeDetails(loginByPINResponseItem)
                                Log.d(
                                    "TAGGGGGGGGGGG",
                                    "onResponse: data saved successfully successfully in login mob screen"
                                )
                                val intent =
                                    Intent(this@LoginByPinMobileScreen, MainActivity::class.java)

                                intent.putExtra(
                                    "mPIN",
                                    binding.employeepinEdt.text.toString().trim()
                                )
                                intent.putExtra(
                                    "empNumber",
                                    binding.employeeMobileEdt.text.toString().trim()
                                )
                                startActivity(intent)

                            }

                        }

                        else {
                            Toast.makeText(this@LoginByPinMobileScreen, "you does not have valid pin", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {

                    }
                })


            } else {
                Toast.makeText(this, "Please enter valid mobile or pin", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
        }


    }
}