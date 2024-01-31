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
import com.example.ifmapp.databinding.ActivitySignUpScreenBinding
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInScreen : AppCompatActivity() {

    private lateinit var retrofitInstance: ApiInterface
    private lateinit var binding: ActivitySignUpScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen)
        retrofitInstance = RetrofitInstance.apiInstance

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_screen)

        binding.btnContinue.isEnabled = false

        binding.btnContinue.setOnClickListener {
            binding.employeepinEdt.isClickable = false
            binding.employeeidEdt.isClickable = false
         /*   val stringWithoutSpaces = binding.employeeidEdt.toString().replace(" ", "")
            if (binding.employeeidEdt.toString() != stringWithoutSpaces) {
                binding.employeeidEdt.setText(stringWithoutSpaces)
                binding.employeeidEdt.setSelection(stringWithoutSpaces.length) // Move cursor to the end
            }*/
            loginByEmployeeId(
                binding.employeeidEdt.text.toString().trim(),
                binding.employeepinEdt.text.toString().trim()
            )

        }
        binding.employeepinEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.employeeidEdt.text.toString().trim().length <=12
                    && binding.employeepinEdt.text.toString().trim().length == 4
                ) {
                    binding.btnContinue.isEnabled = true

                    binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                    binding.btnContinue.setTextColor(resources.getColor(R.color.white))

                }
            }

            override fun afterTextChanged(s: Editable?) {
                val stringWithoutSpaces = s.toString().replace(" ", "")
              /*  if (s.toString() != stringWithoutSpaces) {
                    binding.employeepinEdt.setText(stringWithoutSpaces)
                    binding.employeepinEdt.setSelection(stringWithoutSpaces.length) // Move cursor to the end
                }*/
            }
        })
    }
    private fun loginByEmployeeId(empId: String, pin: String) {

        retrofitInstance.loginByemployeeId("sams", empId, pin)
            .enqueue(object : Callback<LoginByPINResponse?> {
                override fun onResponse(
                    call: Call<LoginByPINResponse?>,
                    response: Response<LoginByPINResponse?>
                ) {
                    if (response.isSuccessful) {
                        Log.d("TAGGGGGGGGG", "onResponse: here")
                        if (response.body()?.get(0)?.MessageID.toString().toInt() == 1) {

                            val user = UserListModel(
                                response.body()?.get(0)?.EmpName.toString(),
                                pin,
                                empId,
                                response.body()?.get(0)?.LocationAutoID.toString(),
                                response.body()?.get(0)?.Designation.toString()
                            )

                            SaveUsersInSharedPreference.addUserIfNotExists(
                                this@SignInScreen, user, pin,response.body()?.get(0)?.EmpName.toString()
                            )
                            Log.d("TAGGGGGGGGG", "onResponse: user saved successfully")

                            val intent =
                                Intent(
                                    this@SignInScreen,
                                    MainActivity::class.java
                                )
                            intent.putExtra("pinFromSignin", pin)
                            startActivity(intent)
                        } else {
                          CustomToast.showToast(this@SignInScreen,response.body()?.get(0)?.MessageString.toString())
                        }

                    } else {
                        Log.d("TAGGGGGG", "onResponse: pin generation is no successful")
                    }
                }

                override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {

                }
            })


    }



}
