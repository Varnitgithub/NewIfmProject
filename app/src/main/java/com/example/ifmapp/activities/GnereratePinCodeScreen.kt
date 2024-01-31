package com.example.ifmapp.activities


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityGnereratePinCodeScreenBinding
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GnereratePinCodeScreen : AppCompatActivity() {
    private lateinit var edtPincodeLiveData: MutableLiveData<Int>
    private lateinit var binding: ActivityGnereratePinCodeScreenBinding
    private var mobileNumber: String? = null
    private lateinit var retrofitInstance: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_enter_pin_code_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gnererate_pin_code_screen)
        edtPincodeLiveData = MutableLiveData()
        retrofitInstance = RetrofitInstance.apiInstance
        mobileNumber = intent.getStringExtra("mobileNumber")



        edtPincodeLiveData.observe(this) {
            if (it == 4) {
                binding.btnGenerate.setBackgroundResource(R.drawable.button_back)
                binding.btnGenerate.setTextColor(resources.getColor(R.color.white))
            } else {
                binding.btnGenerate.setBackgroundResource(R.drawable.site_selection_back)
                binding.btnGenerate.setTextColor(resources.getColor(R.color.btn_continue))
            }
        }



        binding.btnGenerate.setOnClickListener {
            binding.edtEnterPinCode.isEnabled = false
            generatePIN()

        }

        binding.edtEnterPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0
                edtPincodeLiveData.postValue(currentLength)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun generatePIN() {
        if (binding.edtEnterPinCode.text != null) {


            retrofitInstance.pinGeneraton(
                "sams",
                mobileNumber ?: "",
                binding.edtEnterPinCode.text.toString().trim()
            ).enqueue(object : Callback<VerifyOtpResponse?> {
                override fun onResponse(
                    call: Call<VerifyOtpResponse?>,
                    response: Response<VerifyOtpResponse?>
                ) {
                    if (response.isSuccessful) {

if (response.body()?.get(0)?.MessageID?.toInt()==1){

    retrofitInstance.loginByPIN(
        "sams",
        mobileNumber.toString(),
        binding.edtEnterPinCode.text.toString().trim()
    ).enqueue(object : Callback<LoginByPINResponse?> {
        override fun onResponse(
            call: Call<LoginByPINResponse?>,
            response: Response<LoginByPINResponse?>
        ) {
            if (response.isSuccessful && response.body() != null) {
                if (response.body()?.get(0)?.MessageID?.toInt()==1){
                    val user = UserListModel(
                        response.body()?.get(0)?.EmpName.toString(),
                        binding.edtEnterPinCode.text.toString().trim(),
                        response.body()?.get(0)?.EmpNumber.toString(),
                        response.body()?.get(0)?.LocationAutoID.toString(),
                        response.body()?.get(0)?.Designation.toString()
                    )

                    SaveUsersInSharedPreference.addUserIfNotExists(
                        this@GnereratePinCodeScreen,
                        user,binding.edtEnterPinCode.text.toString().trim(),
                    )
                    startActivity(Intent(this@GnereratePinCodeScreen,DashBoardScreen::class.java))


                    Log.d(
                        "TAGGGGGG",
                        "onResponse: data called and saved successful"
                    )
                }else{
                    CustomToast.showToast(this@GnereratePinCodeScreen,response.body()?.get(0)?.MessageString.toString())
                }


            }
        }

        override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {

        }
    })
    val intent = Intent(
        this@GnereratePinCodeScreen,
        DashBoardScreen::class.java)

    startActivity(intent)
    Log.d("TAGGGGGG", "onResponse: response is successful")
}else{
    CustomToast.showToast(this@GnereratePinCodeScreen,response.body()?.get(0)?.MessageString.toString())
}

                    } else {
                        binding.edtEnterPinCode.isEnabled = true
                        Log.d("TAGGGGGG", "onResponse: response is not successful")
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
                    binding.edtEnterPinCode.isEnabled = true
                    Log.d("TAGGGGGGGG", "onFailure: pin generation failed")
                }
            })
        } else {
           CustomToast.showToast(this@GnereratePinCodeScreen,"Please enter 4 digit pin")
        }
    }

}