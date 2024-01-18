package com.example.ifmapp.apiinterface

import com.example.ifmapp.modelclasses.verifymobile.InputMobileRegister
import com.example.ifmapp.modelclasses.verifymobile.OtpSend
import com.example.ifmapp.modelclasses.verifymobile.VerifyMobileNumber
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @POST("GroupLApp.asmx")
    fun registeredMobileNumber(@Query("op")inputMobileRegister: InputMobileRegister):Call<OtpSend>

    @POST("GroupLApp.asmx")
    fun verifyMobileNumber(@Query("op")verifyMobileNumber: VerifyMobileNumber):Call<Void>


}

