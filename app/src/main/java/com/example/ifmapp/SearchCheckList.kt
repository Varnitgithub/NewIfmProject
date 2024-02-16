package com.example.ifmapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.activities.checklists.CheckListForHousekeeping
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivitySearchCheckListBinding
import com.example.ifmapp.databinding.HouseKeepingItemsBinding
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.header_list_response_model.HeaderResponseModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.ShiftDetailsObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchCheckList : AppCompatActivity() {
    private lateinit var binding: ActivitySearchCheckListBinding
    private lateinit var headerList: ArrayList<String>
    private var headerSelect: String? = null
    private var siteSelect: String? = null
    private lateinit var retrofitInstance: ApiInterface

    private var position: String? = null
    private var tourCode: String? = null
    private lateinit var headerIDSelect: ArrayList<Pair<String, String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_search_check_list)

        binding = DataBindingUtil.setContentView(
            this@SearchCheckList,
            R.layout.activity_search_check_list
        )
        headerList = ArrayList()
        headerIDSelect = ArrayList()
        retrofitInstance = RetrofitInstance.apiInstance
        siteSelect = intent.getStringExtra("siteSelect")
        position = intent.getStringExtra("position")
        tourCode = intent.getStringExtra("tourCode")
        Log.d("TAGGGGGGGGGGG", "onCreate: 111.......$tourCode")
        if (siteSelect != null) {
            getHeaderFromServer(siteSelect.toString())
            Log.d("HEADER", "onCreate: HEADER $siteSelect")

        }
        binding.btnContinue.setOnClickListener {
            val intent = Intent(this@SearchCheckList, CheckListForHousekeeping::class.java)
            intent.putExtra("headerId", headerSelect)
            intent.putExtra("siteSelect", siteSelect)
            intent.putExtra("position", position)
            intent.putExtra("tourCode", tourCode)
            startActivity(intent)
        }
    }


    fun setSiteSelection(
        sites: ArrayList<String>
    ) {
        val _SiteList = ArrayList<String>()
        _SiteList.clear()
        _SiteList.addAll(sites)

        val adapterSelectionShift = ArrayAdapter(
            this@SearchCheckList,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            ArrayList<String>()
        )
        adapterSelectionShift.addAll(_SiteList)

        adapterSelectionShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectSite.adapter = adapterSelectionShift

        binding.spinnerSelectSite.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position)
                    if (selectedItem != null) {
                        var mSelectedheaderId = getClientHeaderBySiteName(selectedItem.toString())
                        headerSelect = mSelectedheaderId
                        Log.d("TAGGGGG", "onItemSelected: this condition $mSelectedheaderId")
                    } else {
                        Log.d("TAGGGGG", "onItemSelected: this condition null")

                        siteSelect == ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {

                        }
                    }
                }
            }
    }


    private fun getHeaderFromServer(
        selectSite: String
    ) {
        headerList.clear()
        retrofitInstance.getChecklistHeader(
            "sams", selectSite
        ).enqueue(object : Callback<HeaderResponseModel?> {
            override fun onResponse(
                call: Call<HeaderResponseModel?>,
                response: Response<HeaderResponseModel?>
            ) {
                if (response.isSuccessful) {
                    val sizes = response.body()?.size?.minus(1)
                    for (i in 0..sizes!!) {
                        headerList.add(response.body()!!.get(i).ChecklistHeader)
                        headerIDSelect.add(
                            response.body()?.get(i)!!.ChecklistHeader to response.body()
                                ?.get(i)!!.ChecklistHeaderAutoID
                        )
                    }
                    setSiteSelection(headerList)
                }
            }

            override fun onFailure(
                call: Call<HeaderResponseModel?>,
                t: Throwable
            ) {

            }
        })
    }

    fun getClientHeaderBySiteName(headerName: String): String? {
        for (pair in headerIDSelect) {
            if (pair.first == headerName) {
                return pair.second // Return ClientCode if ClientSiteName matches
            }
        }
        return null // Return null if no match is found
    }
}