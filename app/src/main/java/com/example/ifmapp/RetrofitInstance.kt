package com.example.ifmapp

import com.example.ifmapp.apiinterface.ApiInterface
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://ifm360.in/GroupL/webservices/GroupLApp.asmx/"
object RetrofitInstance {

    private val retrofit:Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create (
            GsonBuilder().setLenient().create())).build()
    }

    val apiInstance :ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}