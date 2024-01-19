package com.example.ifmapp.apiinterface

import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.verifymobile.OtpSend
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {


    @FormUrlEncoded
    @POST("ValidateEmployeeByMobile")
    fun validateMobileNumber(
        @Field("connectionKey") connectionKey: String,
        @Field("mobileNumber") mobileNumber: String
    ): Call<VerifyOtpResponse>

    @FormUrlEncoded
    @POST("SendOTP")
    fun registeredMobileNumber(
        @Field("connectionKey") connectionKey: String,
        @Field("mobileNumber") mobileNumber: String
    ): Call<OtpSend>

    @FormUrlEncoded
    @POST("PinGeneration")
    fun pinGeneraton(
        @Field("connectionKey") connectionKey: String,
        @Field("mobileNumber") mobileNumber: String,
        @Field("PIN") PIN:String):Call<VerifyOtpResponse>


    @FormUrlEncoded
    @POST("LoginByPin")
    fun loginByPIN(
        @Field("connectionKey") connectionKey: String,
        @Field("mobileNumber") mobileNumber: String,
        @Field("PIN") PIN:String):Call<LoginByPINResponse>

    @FormUrlEncoded
    @POST("VerifyOTP")
    fun verifyMobileNumber(
        @Field("connectionKey") connectionKey: String,
        @Field("mobileNumber") mobileNumber: String,
        @Field("OTP") OTP: String
    ): Call<VerifyOtpResponse>


}

