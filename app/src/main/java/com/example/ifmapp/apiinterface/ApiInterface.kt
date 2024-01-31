package com.example.ifmapp.apiinterface

import com.example.ifmapp.modelclasses.attendance_response.AttendanceResponse
import com.example.ifmapp.modelclasses.daily_attendance_model.DailyAttendanceModel
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponse
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponseItem
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
    @POST("ValidateEmployeeByEmployeeID")
    fun validateEmployeeId(
        @Field("connectionKey") connectionKey: String,
        @Field("EmpId") EmpId: String
    ): Call<VerifyOtpResponse>

    @FormUrlEncoded
    @POST("PinGenerationByEmpID")
    fun pinGenerationByEmpId(
        @Field("connectionKey") connectionKey: String,
        @Field("EmpID") EmpID: String,
        @Field("PIN") PIN:String):Call<VerifyOtpResponse>

    @FormUrlEncoded
    @POST("LoginByEmpID")
    fun loginByemployeeId(
        @Field("connectionKey") connectionKey: String,
        @Field("EmpID") EmpID: String,
        @Field("PIN") PIN:String):Call<LoginByPINResponse>

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


    @FormUrlEncoded
    @POST("GetStandardShifts")
    fun shiftSelectionApi(
        @Field("connectionKey") connectionKey: String,
        @Field("LocationAutoID") LocationAutoID: String,
       ):Call<ShiftSelectionResponse>

    @FormUrlEncoded
    @POST("GetGeoMappedSites")
    fun getGeoMappedSites(
        @Field("connectionKey") connectionKey: String,
        @Field("LocationAutoID") LocationAutoID: String,
        @Field("Latitude") Latitude: String,
        @Field("Longitude") Longitude: String,
    ):Call<GeoMappedResponse>


    @FormUrlEncoded
    @POST("GetEmployeeAttendanceDaily")
    fun getAttendancDaily(
        @Field("connectionKey") connectionKey: String,
        @Field("ClientCode") ClientCode: String,
        @Field("AsmtId") AsmtId: String,
        @Field("shift") shift: String,
        @Field("EmployeeNumber") EmployeeNumber: String
    ):Call<DailyAttendanceModel>


    @FormUrlEncoded
    @POST("InsertEmployeeAttendance")
    fun insertAttendance(
        @Field("connectionKey") connectionKey: String,
        @Field("IMEI") IMEI: String,
        @Field("userId") userId: String,
        @Field("AsmtID") AsmtID: String,
        @Field("employeeNumber") employeeNumber: String,
        @Field("InOutStatus") InOutStatus: String,
        @Field("DutyDateTime") DutyDateTime: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("altitude") altitude: String,
        @Field("employeeImageBase64") employeeImageBase64: String,
        @Field("LocationAutoId") LocationAutoId: String,
        @Field("ClientCode") ClientCode: String,
        @Field("ShiftCode") ShiftCode: String,
        @Field("LocationName") LocationName: String,
    ):Call<AttendanceResponse>

}

