package com.example.ifmapp.databasedb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ifmapp.modelclasses.EmployeeDetails
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem

@Dao
interface EmployeePinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployeePin(employeePin: EmployeePin)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployeeDetails(loginByPINResponse: LoginByPINResponse)

    @Query("SELECT * FROM EmployeeDetailsTable")
    fun getEmployeeDetails():LiveData<LoginByPINResponseItem>

    @Query("SELECT * FROM EmployeePin WHERE PIN= :enteredPin")
    fun getEmployeePIN(enteredPin: String): LiveData<EmployeePin>
}