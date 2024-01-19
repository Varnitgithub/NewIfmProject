package com.example.ifmapp.apiinterface

import com.example.ifmapp.modelclasses.verifymobile.OtpSend
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("SendOTP")
    fun registeredMobileNumber(@Field("connectionKey") connectionKey: String,
                               @Field("mobileNumber") mobileNumber: String):Call<OtpSend>
    @FormUrlEncoded
    @POST("GroupLApp.asmx")
    fun verifyMobileNumber(@Field("connectionKey") connectionKey: String,
                           @Field("mobileNumber") mobileNumber: String,
                           @Field("OTP") OTP:String): Call<Void>


}

