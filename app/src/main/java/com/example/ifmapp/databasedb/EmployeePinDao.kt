package com.example.ifmapp.databasedb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ifmapp.modelclasses.ShiftTimingDetails
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem

@Dao
interface EmployeePinDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployeeDetails(loginByPINResponseItem: LoginByPINResponseItem)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShiftTiming(shiftTimingDetails: ShiftTimingDetails)

    @Query("SELECT * FROM EmployeeDetailsTable")
    fun getAllEmployeeDetails():LiveData<List<LoginByPINResponseItem>>


    @Query("SELECT * FROM EmployeeDetailsTable ORDER BY id ASC LIMIT 1")
    fun getFirstEmployeeDetails(): LiveData<LoginByPINResponseItem>

    @Query("SELECT * FROM EmployeeDetailsTable WHERE pin = :mypin LIMIT 1")
    fun getcurrentEmployeeDetails(mypin: String): LiveData<LoginByPINResponseItem>

    @Query("SELECT * FROM ShiftTiming")
    fun getShiftTimingDetails(): LiveData<ShiftTimingDetails>

//    @Query("SELECT * FROM EmployeePin WHERE PIN= :enteredPin")
//    fun getCurrentEmployeePIN(enteredPin: String): LiveData<EmployeePin>


}