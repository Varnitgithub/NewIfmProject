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
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePin
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.ActivityGnereratePinCodeScreenBinding
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GnereratePinCodeScreen : AppCompatActivity() {
    private lateinit var edtPincodeLiveData: MutableLiveData<Int>
    private lateinit var binding: ActivityGnereratePinCodeScreenBinding
    private var mobileNumber: String? = null
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var employeePinDao: EmployeePinDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_enter_pin_code_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gnererate_pin_code_screen)
        edtPincodeLiveData = MutableLiveData()
        retrofitInstance = RetrofitInstance.apiInstance
        mobileNumber = intent.getStringExtra("mobileNumber")


        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()

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

                        val intent = Intent(
                                this@GnereratePinCodeScreen,
                                DashBoardScreen::class.java
                            )
                        intent.putExtra("usermobileNumber",mobileNumber)
                        intent.putExtra("PIN",binding.edtEnterPinCode.text.toString().trim())
                        startActivity(intent)


                        Log.d("TAGGGGGG", "onResponse: response is successful")

                    } else {
                        Log.d("TAGGGGGG", "onResponse: response is not successful")
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
                    Log.d("TAGGGGGGGG", "onFailure: pin generation failed")
                }
            })
        } else {
            Toast.makeText(
                this@GnereratePinCodeScreen,
                "Please enter 4 digit pin",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}