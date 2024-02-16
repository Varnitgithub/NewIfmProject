package com.example.ifmapp.apiinterface

import com.example.ifmapp.activities.checklists.checklist__model.CheckListModel
import com.example.ifmapp.activities.checklists.checklist__model.ImageAddingModel
import com.example.ifmapp.activities.checklists.housekeeping_model.ViewPhotoResponse
import com.example.ifmapp.activities.tasks.taskapi_response.TaskApiResponse
import com.example.ifmapp.modelclasses.attendance_response.AttendanceResponse
import com.example.ifmapp.modelclasses.daily_attendance_model.DailyAttendanceModel
import com.example.ifmapp.modelclasses.document_images_model.DocumentImagesModel
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.header_list_response_model.HeaderResponseModel
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.postmodel.PostModel
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponse
import com.example.ifmapp.modelclasses.shiftwithtime_model.ShiftWithTimeResponse
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
    fun  shiftSelectionApi(
        @Field("connectionKey") connectionKey: String,
        @Field("LocationAutoID") LocationAutoID: String,
       ):Call<ShiftSelectionResponse>

    @FormUrlEncoded
    @POST("GetEmpMappedSites")
    fun getGeoMappedSites(
        @Field("connectionKey") connectionKey: String,
        @Field("EmpID") EmpID: String,
        @Field("LocationAutoId") LocationAutoID: String,
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
        @Field("Post") Post: String,
    ):Call<AttendanceResponse>


    @FormUrlEncoded
    @POST("GetEmployeeAttendanceDailyWithShift")
    fun getAttendancDailyWithShift(
        @Field("connectionKey") connectionKey: String,
        @Field("ClientCode") ClientCode: String,
        @Field("AsmtId") AsmtId: String,
        @Field("LocationAutoID") LocationAutoID: String,
        @Field("EmployeeNumber") EmployeeNumber: String
    ):Call<ShiftWithTimeResponse>

    @FormUrlEncoded
    @POST("GetClientTourCode")
    fun getTourTasks(
        @Field("connectionKey") connectionKey: String,
        @Field("clientCode") clientCode: String,
    ):Call<TaskApiResponse>


    @FormUrlEncoded
    @POST("GetChecklistHeader")
    fun getChecklistHeader(
        @Field("connectionKey") connectionKey: String,
        @Field("clientCode") clientCode: String,
    ):Call<HeaderResponseModel>

    @FormUrlEncoded
    @POST("GetEmployeeDocs")
    fun getEmployeeDocs(
        @Field("connectionKey") connectionKey: String,
        @Field("EmpID") EmpID: String,
        @Field("LocationAutoID") LocationAutoID: String,
        @Field("DocType") DocType: String
    ):Call<DocumentImagesModel>


    @FormUrlEncoded
    @POST("InsertRegisterEntry")
    fun insertRegisterEntry(
        @Field("connectionKey") connectionKey: String,
        @Field("EmployeeID") EmployeeID: String,
        @Field("EmployeeName") EmployeeName: String,
        @Field("VisitorImageBase64") VisitorImageBase64: String,
        @Field("LocationAutoId") LocationAutoId: String,
        @Field("ClientCode") ClientCode: String,
        @Field("VisitorName") VisitorName: String,
        @Field("Purpose") Purpose: String,
        @Field("Mobile") Mobile: String,
    ):Call<VerifyOtpResponse>

    @FormUrlEncoded
    @POST("InsertClientChecklistImage")
    fun insertCheckListImageHouseKeeping(
        @Field("connectionKey") connectionKey: String,
        @Field("ClientCode") ClientCode: String,
        @Field("TourAutoId") TourAutoId: String,
        @Field("ChecklistHeaderAutoID") ChecklistHeaderAutoID: String,
        @Field("ChecklistAutoID") ChecklistAutoID: String,
        @Field("ChecklistImageBase64") ChecklistImageBase64: String,
    ):Call<ImageAddingModel>

    @FormUrlEncoded
    @POST("UpdateChecklistStatus")
    fun updateChecklistStatustoCompletedHouseKeeping(
        @Field("connectionKey") connectionKey: String,
        @Field("ClientCode") ClientCode: String,
        @Field("TourAutoId") TourAutoId: String,
        @Field("ChecklistHeaderAutoID") ChecklistHeaderAutoID: String,
        @Field("ChecklistAutoID") ChecklistAutoID: String,
        @Field("EmpCode") EmpCode: String,
        @Field("Remarks") Remarks: String,
    ):Call<ImageAddingModel>
    @FormUrlEncoded
    @POST("GetClientChecklistImage")
    fun getChecklistImageUpdatedHouseKeeping(
        @Field("connectionKey") connectionKey: String,
        @Field("ClientCode") ClientCode: String,
        @Field("TourAutoId") TourAutoId: String,
        @Field("ChecklistHeaderAutoID") ChecklistHeaderAutoID: String,
        @Field("ChecklistAutoID") ChecklistAutoID: String,



    ):Call<ViewPhotoResponse>

    @FormUrlEncoded
    @POST("GetSitesPost")
    fun getSitesPost(
        @Field("connectionKey") connectionKey: String,
        @Field("LocationAutoID") LocationAutoID: String,
        @Field("ClientCode") ClientCode: String,
        @Field("AsmtId") AsmtId: String,
    ):Call<PostModel>

    @FormUrlEncoded
    @POST("GetGeoMappedSitesBasisOfPost")
    fun getGeoMappedSitesBasisOfPost(
        @Field("connectionKey") connectionKey: String,
        @Field("LocationAutoID") LocationAutoID: String,
        @Field("Latitude") Latitude: String,
        @Field("Longitude") Longitude: String,

        @Field("ClientCode") ClientCode: String,
        @Field("AsmtID") AsmtID: String,
        @Field("PostAutoID") PostAutoID: String,
    ):Call<VerifyOtpResponse>


    @FormUrlEncoded
    @POST("GetChecklistName")
    fun getChecklistName(
        @Field("connectionKey") connectionKey: String,
        @Field("ClientCode") ClientCode: String,
        @Field("TourAutoId") TourAutoId: String,
        @Field("ChecklistHeaderAutoID") ChecklistHeaderAutoID: String,
        @Field("EmpID") EmpID: String
    ):Call<CheckListModel>

}

