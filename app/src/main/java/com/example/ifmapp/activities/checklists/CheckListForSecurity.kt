package com.example.ifmapp.activities.checklists

import CheckListAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityCheckListForSecurityBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.UserObject

class CheckListForSecurity : AppCompatActivity(), CheckListAdapter.Clicked {
    private lateinit var binding:ActivityCheckListForSecurityBinding
    private lateinit var checkListAdapter: CheckListAdapter
    private var empId: String? = null
    private var empName: String? = null
    private var pin: String? = null
    private lateinit var retrofitInstance: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list_for_security)
        binding = DataBindingUtil.setContentView(this@CheckListForSecurity,R.layout.activity_check_list_for_housekeeping)
        checkListAdapter = CheckListAdapter(this,this)
        binding.houseKeepingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.houseKeepingRecyclerView.adapter = checkListAdapter

        empId = intent.getStringExtra("empId")

        for (users in SaveUsersInSharedPreference.getList(this@CheckListForSecurity)) {
            if (users.empId == UserObject.userId) {
                empId = users.empId
                empName = users.userName
                pin = users.pin
            }
        }

        if (SaveUsersInSharedPreference.getHindiEnglish(this@CheckListForSecurity)=="hindi"){
            val checklist = checklistDataInHindi()
            checkListAdapter.updateList(checklist)
        }else{
            val checklist = checklistData()
            checkListAdapter.updateList(checklist)
        }


    }



    private fun checklistData():ArrayList<CheckListModel>{
        var checklist = ArrayList<CheckListModel>()

        checklist.add(CheckListModel(1,"Is ID Card available","pending",true))
        checklist.add(CheckListModel(2,"Office floor are free of stairs\n" +
                "& orderlessAre you in Uniform","pending",true))
        checklist.add(CheckListModel(3,"Is the lock sealed correctly","pending",true))
        checklist.add(CheckListModel(4,"Is court notice display","pending",true))

        return  checklist
    }

    private fun checklistDataInHindi():ArrayList<CheckListModel>{
        var checklist = ArrayList<CheckListModel>()

        checklist.add(CheckListModel(1,"क्या आईडी कार्ड उपलब्ध है","लंबित",true))
        checklist.add(CheckListModel(2,"कार्यालय के फर्श पर सीढ़ियाँ नहीं हैं\n"+
                "और व्यवस्थित क्या आप वर्दी में हैं", "लंबित",true))
        checklist.add(CheckListModel(3,"क्या ताला सही ढंग से सील किया गया है", "लंबित",true))
        checklist.add(CheckListModel(4,"क्या न्यायालय नोटिस प्रदर्शित किया जाना है", "लंबित",true))

        return  checklist
    }


    override fun onAddPhotoClick(checkListModel: CheckListModel, position: Int) {
    }

    override fun onViewPhotoClick(checkListModel: CheckListModel, position: Int) {

    }

    override fun onSwitchOnClick(checkListModel: CheckListModel, position: Int) {

    }


    override fun onBackPressed() {
        val intent = Intent(this@CheckListForSecurity, MainActivity::class.java)
        intent.putExtra("mPIN", pin)
        intent.putExtra("empName", empName)
        intent.putExtra("empId", empId)
        startActivity(intent)
        super.onBackPressed()
    }
}