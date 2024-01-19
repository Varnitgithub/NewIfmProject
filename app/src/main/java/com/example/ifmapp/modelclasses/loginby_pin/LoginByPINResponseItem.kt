package com.example.ifmapp.modelclasses.loginby_pin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("EmployeeDetailsTable")
data class LoginByPINResponseItem(
   @PrimaryKey(autoGenerate = true) var id:Int=0 ,
    val Designation: String="",
    val EmpName: String="",
    val EmpNumber: String="",
    val LocationAutoID: String="",
    val MessageID: String="",
    val MessageString: String="",
 var pin:String="",
 var mobileNumber:String=""
)