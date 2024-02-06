package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.UnderlineSpan
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
        binding.progressBar.visibility = View.GONE
        binding.btnGenerate.isEnabled = false
        binding.progressBar.visibility = View.GONE
        val textToUnderline = binding.validatePin.text.toString()
        binding.validatePin.visibility = View.VISIBLE
        // Create a SpannableString with the text you want to underline
        val underlinedText = SpannableString(textToUnderline)

        // Apply the UnderlineSpan to the entire text
        underlinedText.setSpan(
            UnderlineSpan(),
            0,
            textToUnderline.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        // Set the SpannableString to the TextView
        binding.validatePin.text = underlinedText

        binding.btnGenerate.setOnClickListener {
            binding.employeeidEdt.isClickable = false
            binding.employeeidEdt.isEnabled = false
            pinGenerationByEmployeeId(
                "sams",
                binding.employeeidEdt.text.toString().trim(),
                binding.employeepinEdt.text.toString().trim()
            )
            binding.btnGenerate.isClickable = false
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
            if (binding.employeeidEdt.text.toString().length >= 2) {
                binding.employeeidEdt.isClickable = false
                binding.employeeidEdt.isEnabled = false
                validateEmployeeId(binding.employeeidEdt.text.toString().trim())

            }else{
                CustomToast.showToast(this@SignUpWithoutMobile,"Please Enter Employee Id")
            }

        }

        binding.employeeidEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.validatePin.visibility = View.VISIBLE
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
                            binding.employeeidEdt.isClickable = true
                            binding.employeeidEdt.isEnabled = true
                            CustomToast.showToast(
                                this@SignUpWithoutMobile,
                                response.body()?.get(0)?.MessageString.toString()
                            )
                        }


                    } else {
                        binding.employeeidEdt.isClickable = true
                        binding.employeeidEdt.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
                    binding.employeeidEdt.isClickable = true
                    binding.employeeidEdt.isEnabled = true
                }
            })
    }

    private fun pinGenerationByEmployeeId(connectionKey: String, empId: String, pin: String) {

        binding.progressBar.visibility = View.VISIBLE

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
                            binding.btnGenerate.isClickable = true


                        }

                    } else {
                        binding.btnGenerate.isClickable = true

                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
                    binding.btnGenerate.isClickable = true

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
                                response.body()?.get(0)?.EmpNumber.toString()
                            )

                            val intent =
                                Intent(
                                    this@SignUpWithoutMobile,
                                    DashBoardScreen::class.java
                                )
                            intent.putExtra("userPin", pin)

                            startActivity(intent)
                            binding.progressBar.visibility = View.GONE

                        } else {
                            CustomToast.showToast(
                                this@SignUpWithoutMobile,
                                response.body()?.get(0)?.MessageString.toString()
                            )
                            binding.btnGenerate.isClickable = true
                        }
                    } else {
                        binding.btnGenerate.isClickable = true
                    }
                }

                override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {
                    binding.btnGenerate.isClickable = true
                }
            })
    }

}