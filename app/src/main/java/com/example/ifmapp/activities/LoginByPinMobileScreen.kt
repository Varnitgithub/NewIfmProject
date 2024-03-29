package com.example.ifmapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityLoginByPinMobileScreenBinding
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginByPinMobileScreen : AppCompatActivity() {
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var binding: ActivityLoginByPinMobileScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_by_pin_mobile_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_by_pin_mobile_screen)
        retrofitInstance = RetrofitInstance.apiInstance
        binding.progressBar.visibility = View.GONE
        binding.btnContinue.setOnClickListener {
            loginUser()
        }

        binding.employeepinEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.employeeMobileEdt.text.toString().length == 10 && s?.length == 4) {

                    binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                    binding.btnContinue.setBackgroundResource(R.drawable.button_back)

                } else {
                    binding.btnContinue.setTextColor(resources.getColor(R.color.check_btn))
                    binding.btnContinue.setBackgroundResource(R.drawable.button_backwhite)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                /*  val stringWithoutSpaces = s.toString().replace(" ", "")
                  if (s.toString() != stringWithoutSpaces) {
                      binding.employeepinEdt.setText(stringWithoutSpaces)
                      binding.employeepinEdt.setSelection(stringWithoutSpaces.length) // Move cursor to the end
                  }*/
            }
        })

    }

    private fun loginUser() {
        binding.progressBar.visibility = View.VISIBLE

       // val stringWithoutSpaces = binding.employeeMobileEdt.toString().replace(" ", "")
//        if (binding.employeeMobileEdt.toString() != stringWithoutSpaces) {
//            binding.employeepinEdt.setText(stringWithoutSpaces)
//            binding.employeepinEdt.setSelection(stringWithoutSpaces.length) // Move cursor to the end
//        }

        if (binding.employeeMobileEdt.text != null && binding.employeepinEdt.text != null) {

            if (binding.employeeMobileEdt.text.toString().length <= 10 && binding.employeepinEdt.text.toString().length <= 4) {
               binding.employeeMobileEdt.isClickable = false
                binding.employeepinEdt.isClickable = false
                retrofitInstance.loginByPIN(
                    "sams",
                    binding.employeeMobileEdt.text.toString().trim(),
                    binding.employeepinEdt.text.toString()
                ).enqueue(object : Callback<LoginByPINResponse?> {
                    override fun onResponse(
                        call: Call<LoginByPINResponse?>,
                        response: Response<LoginByPINResponse?>
                    ) {
                        if (response.isSuccessful
                        ) {

                            if (response.body()
                                    ?.get(0)?.MessageID?.toInt() == 1
                            ) {

                                val userPin = binding.employeepinEdt.text.toString().trim()
                                val usersList =
                                    UserListModel(
                                        response.body()!![0].EmpName,
                                        userPin,
                                        response.body()!![0].EmpNumber,
                                        response.body()?.get(0)?.LocationAutoID.toString(),
                                        response.body()?.get(0)?.Designation.toString()

                                    )


                                SaveUsersInSharedPreference.addUserIfNotExists(
                                    this@LoginByPinMobileScreen,
                                    usersList,
                                    binding.employeepinEdt.text.toString(),
                                    response.body()?.get(0)?.EmpNumber.toString()
                                )
                                val intent =
                                    Intent(this@LoginByPinMobileScreen, MainActivity::class.java)
                                intent.putExtra(
                                    "mPINFromLogin",
                                    binding.employeepinEdt.text.toString().trim()
                                )
                                intent.putExtra(
                                    "empId",
                                   response.body()?.get(0)!!.EmpNumber.toString()
                                )
                                intent.putExtra(
                                    "empName",
                                   response.body()?.get(0)!!.EmpName.toString()
                                )
                                startActivity(intent)
                                binding.progressBar.visibility = View.GONE

                                binding.employeepinEdt.text.clear()
                                binding.employeeMobileEdt.text.clear()
                            } else {
                                CustomToast.showToast(
                                    this@LoginByPinMobileScreen,
                                    response.body()?.get(0)?.MessageString.toString()
                                )
                                binding.employeeMobileEdt.isClickable = true
                                binding.employeepinEdt.isClickable = true
                                binding.progressBar.visibility = View.GONE

                            }

                        } else {
                            CustomToast.showToast(
                                this@LoginByPinMobileScreen,
                                "You does not have valid pin or Mobile no."
                            )
                            binding.employeeMobileEdt.isClickable = true
                            binding.employeepinEdt.isClickable = true
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                    override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {
                        binding.progressBar.visibility = View.GONE
                        binding.employeeMobileEdt.isClickable = true
                        binding.employeepinEdt.isClickable = true

                    }
                })


            } else {
                CustomToast.showToast(
                    this@LoginByPinMobileScreen,
                    "Please enter valid mobile or pin"
                )
                binding.employeeMobileEdt.isClickable = true
                binding.employeepinEdt.isClickable = true
            }

        } else {
            CustomToast.showToast(this@LoginByPinMobileScreen, "Please enter all details")
            binding.employeeMobileEdt.isClickable = true
            binding.employeepinEdt.isClickable = true
        }


    }
}
