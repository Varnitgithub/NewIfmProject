package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivitySignUpWithoutMobileBinding
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignUpWithoutMobile : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpWithoutMobileBinding

    private lateinit var retrofitInstance: ApiInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sign_up_without_mobile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_without_mobile)
        retrofitInstance = RetrofitInstance.apiInstance

        binding.btnGenerate.isEnabled = false


        binding.btnGenerate.setOnClickListener {
            binding.employeeidEdt.isClickable = false
            pinGenerationByEmployeeId(
                "sams",
                binding.employeeidEdt.text.toString().trim(),
                binding.employeepinEdt.text.toString().trim()
            )
        }

        binding.employeepinEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.employeepinEdt.text.toString().trim().length == 4) {
                    binding.btnGenerate.setBackgroundResource(R.drawable.button_back)
                    binding.btnGenerate.setTextColor(resources.getColor(R.color.white))
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                /*  val stringWithoutSpaces = p0.toString().replace(" ", "")
                  if (p0.toString() != stringWithoutSpaces) {
                      binding.employeepinEdt.setText(stringWithoutSpaces)
                      binding.employeepinEdt.setSelection(stringWithoutSpaces.length) // Move cursor to the end
                  }*/
            }
        })
        binding.validatePin.setOnClickListener {
            if (binding.employeeidEdt.text.toString().length >= 6) {
                validateEmployeeId(binding.employeeidEdt.text.toString().trim())

            }

        }

        binding.employeeidEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                Log.d("TAGGGGGGGGGG", "onTextChanged: onChangeee")


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


    private fun validateEmployeeId(empId: String) {
        retrofitInstance.validateEmployeeId("sams", empId)
            .enqueue(object : Callback<VerifyOtpResponse?> {
                override fun onResponse(
                    call: Call<VerifyOtpResponse?>,
                    response: Response<VerifyOtpResponse?>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.get(0)?.MessageID.toString().toInt() == 1) {

                            binding.employeepinLL.visibility = View.VISIBLE
                            binding.employeepinEdt.inputType = InputType.TYPE_CLASS_NUMBER
                            binding.validatePin.visibility = View.GONE
                            binding.btnGenerate.isEnabled = true

                        } else {
                            CustomToast.showToast(
                                this@SignUpWithoutMobile,
                                response.body()?.get(0)?.MessageString.toString()
                            )
                        }


                    } else {
                        Log.d("TAGGGGGGG", "onResponse: response is not successful")
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
                    Log.d("TAGGGGGGGG", "onFailure: this is falied msg....................")
                }
            })
    }

    private fun pinGenerationByEmployeeId(connectionKey: String, empId: String, pin: String) {

        retrofitInstance.pinGenerationByEmpId(connectionKey, empId, pin)
            .enqueue(object : Callback<VerifyOtpResponse?> {
                override fun onResponse(
                    call: Call<VerifyOtpResponse?>,
                    response: Response<VerifyOtpResponse?>
                ) {
                    if (response.isSuccessful) {

                        if (response.body()?.get(0)?.MessageID.toString().toInt() == 1) {

                            CustomToast.showToast(
                                this@SignUpWithoutMobile,
                                "Pin generation Successful"
                            )
                            loginByEmployeeId("sams", empId, pin)
                        } else {
                            CustomToast.showToast(
                                this@SignUpWithoutMobile,
                                response.body()?.get(0)?.MessageString.toString()
                            )

                        }

                    } else {
                        Log.d("TAGGGGGG", "onResponse: pin generation is no successful")
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {

                }
            })


    }

    private fun loginByEmployeeId(connectionKey: String, empId: String, pin: String) {

        retrofitInstance.loginByemployeeId(connectionKey, empId, pin)
            .enqueue(object : Callback<LoginByPINResponse?> {
                override fun onResponse(
                    call: Call<LoginByPINResponse?>,
                    response: Response<LoginByPINResponse?>
                ) {
                    if (response.isSuccessful) {

                        if (response.body()?.get(0)?.MessageID.toString().toInt() == 1) {
                            val user = UserListModel(
                                response.body()?.get(0)?.EmpName.toString(),
                                pin,
                                empId,
                                response.body()?.get(0)?.LocationAutoID.toString(),
                                response.body()?.get(0)?.Designation.toString()
                            )

                            SaveUsersInSharedPreference.addUserIfNotExists(
                                this@SignUpWithoutMobile,
                                user,
                                pin,
                                response.body()?.get(0)?.EmpName.toString()
                            )

                            val intent =
                                Intent(
                                    this@SignUpWithoutMobile,
                                    DashBoardScreen::class.java
                                )
                            intent.putExtra("userPin", pin)

                            startActivity(intent)
                        } else {
                            CustomToast.showToast(
                                this@SignUpWithoutMobile,
                                response.body()?.get(0)?.MessageString.toString()
                            )
                        }

                    } else {
                        Log.d("TAGGGGGG", "onResponse: pin generation is not successful")
                    }
                }

                override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {

                }
            })


    }

}