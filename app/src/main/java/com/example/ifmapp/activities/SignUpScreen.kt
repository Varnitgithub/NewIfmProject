package com.example.ifmapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivitySignUpScreenBinding
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpScreen : AppCompatActivity() {

    private lateinit var retrofitInstance: ApiInterface
    private lateinit var binding: ActivitySignUpScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen)
        retrofitInstance = RetrofitInstance.apiInstance

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_screen)


        binding.btnContinue.setOnClickListener {
loginByEmployeeId("sams",binding.employeeidEdt.text.toString().trim(),
    binding.employeepinEdt.text.toString().trim())

        }
        binding.employeepinEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.employeeidEdt.text.toString().trim().length == 4
                    && binding.employeepinEdt.text.toString().trim().length == 4
                ) {

                    binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                    binding.btnContinue.setTextColor(resources.getColor(R.color.white))

                }
            }

            override fun afterTextChanged(s: Editable?) {
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
                            var user = UserListModel(
                                response.body()?.get(0)?.EmpName.toString(),
                                pin,
                                empId,
                                ""
                            )

                            SaveUsersInSharedPreference.addUserIfNotExists(
                                this@SignUpScreen, user
                            )

                            startActivity(
                                Intent(
                                    this@SignUpScreen,
                                    DashBoardScreen::class.java
                                )
                            )
                        } else {

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
