package com.example.ifmapp.activities.checklists

import CheckListAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityCheckListForHousekeepingBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference

class CheckListForHousekeeping : AppCompatActivity(), CheckListAdapter.Clicked {

    private var empId: String? = null
    private var empName: String? = null
    private var pin: String? = null
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var binding: ActivityCheckListForHousekeepingBinding
    private lateinit var checkListAdapter: CheckListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_check_list_for_housekeeping)
        binding = DataBindingUtil.setContentView(
            this@CheckListForHousekeeping,
            R.layout.activity_check_list_for_housekeeping
        )
        empId = intent.getStringExtra("empId")

        for (users in SaveUsersInSharedPreference.getList(this@CheckListForHousekeeping)) {
            if (users.empId == empId) {
                empId = users.empId
                empName = users.userName
                pin = users.pin
            }
        }

        checkListAdapter = CheckListAdapter(this, this)
        binding.houseKeepingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.houseKeepingRecyclerView.adapter = checkListAdapter

        if (SaveUsersInSharedPreference.getHindiEnglish(this@CheckListForHousekeeping) == "hindi") {
            val checklist = checklistDataInHindi()
            checkListAdapter.updateList(checklist)
        } else {
            val checklist = checklistData()
            checkListAdapter.updateList(checklist)
        }
    }

    fun checklistData(): ArrayList<CheckListModel> {
        var checklist = ArrayList<CheckListModel>()

        checklist.add(
            CheckListModel(
                1, "Entrance glass clean & free\n" +
                        "from smudges", "pending", true
            )
        )
        checklist.add(
            CheckListModel(
                2, "Office floor are free of stairs\n" +
                        "& orderless", "pending", true
            )
        )
        checklist.add(CheckListModel(3, "Tables, printers are free from dust", "pending", true))
        checklist.add(CheckListModel(4, "The meeting room is clean and odorless", "pending", true))

        checklist.add(CheckListModel(5, "Bathroom is cleaned and odorless", "pending", true))

        checklist.add(
            CheckListModel(
                6,
                "The batihroom is stocked with soap, paper towels and toilet tissue",
                "pending",
                true
            )
        )
        checklist.add(
            CheckListModel(
                7,
                "Sufficient material available at the site",
                "pending",
                true
            )
        )


        return checklist
    }

    fun checklistDataInHindi(): ArrayList<CheckListModel> {
        var checklist = ArrayList<CheckListModel>()

        checklist.add(
            CheckListModel(
                1, "प्रवेश द्वार का शीशा साफ़ और मुफ़्त\n" +
                        "धब्बे से", "लंबित", true
            )
        )
        checklist.add(
            CheckListModel(
                2, "कार्यालय के फर्श पर सीढ़ियाँ नहीं हैं\n" +
                        "और व्यवस्थित", "लंबित", true
            )
        )
        checklist.add(CheckListModel(3, "टेबल, प्रिंटर धूल से मुक्त हैं", "लंबित", true))
        checklist.add(CheckListModel(4, "बैठक कक्ष साफ और गंधहीन है", "लंबित", true))

        checklist.add(
            CheckListModel(
                5,
                "बाथरूम साफ और गंधहीन है", "लंबित", true
            )
        )

        checklist.add(
            CheckListModel(
                6,
                "बाटीरूम में साबुन, कागज़ के तौलिये और शौचालय के टिश्यू भरे हुए हैं",
                "लंबित",
                true
            )
        )
        checklist.add(
            CheckListModel(
                7,
                "साइट पर पर्याप्त सामग्री उपलब्ध हैं", "लंबित", true
            )
        )


        return checklist
    }

    override fun onAddPhotoClick(checkListModel: CheckListModel, position: Int) {

    }

    override fun onViewPhotoClick(checkListModel: CheckListModel, position: Int) {

    }

    override fun onMarkStatusClick(checkListModel: CheckListModel, position: Int) {

    }

    override fun onBackPressed() {
        val intent = Intent(this@CheckListForHousekeeping, MainActivity::class.java)
        intent.putExtra("mPIN", pin)
        intent.putExtra("empName", empName)
        intent.putExtra("empId", empId)
        startActivity(intent)
        super.onBackPressed()
    }

}