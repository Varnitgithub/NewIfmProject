import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.checklists.checklist__model.CheckListModel
import com.example.ifmapp.activities.checklists.checklist__model.CheckListModelItem
import com.example.ifmapp.activities.checklists.checklist__model.ImageAddingModel
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.UserObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckListAdapter(
    private val context: Context, private var listener: Clicked,
    private var siteSelect: String, private var tourCode: String, private var headerSelect: String
) :
    RecyclerView.Adapter<CheckListAdapter.DocumentsViewHolder>() {
    private var retrofitInstance = RetrofitInstance.apiInstance
    var checkList = ArrayList<CheckListModelItem>()

    class DocumentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workTxt: TextView = itemView.findViewById(R.id.work_txt)
        var statusPendingDone: TextView = itemView.findViewById(R.id.statusDone_pending)
        var addPhoto: Button = itemView.findViewById(R.id.btn_addphoto)
        var viewPhoto: Button = itemView.findViewById(R.id.btn_viewPhoto)
        var switchButton: Switch = itemView.findViewById(R.id.switchButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        val view =
            DocumentsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.checklist_for_housekeeping_item, parent, false)
            )

        view.addPhoto.setOnClickListener {
            listener.onAddPhotoClick(checkList[view.adapterPosition], view.adapterPosition)
        }
        view.viewPhoto.setOnClickListener {
            listener.onViewPhotoClick(checkList[view.adapterPosition], view.adapterPosition)
        }
        /* view.switchButton.setOnClickListener {
             listener.onSwitchOnClick(checkList[view.adapterPosition],view.adapterPosition)
         }*/

        return view
    }

    override fun getItemCount(): Int {
        return checkList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
        Log.d("TAGGGGGGGGGGG", "onBindViewHolder: this is header $headerSelect")
        val currentItem = checkList[position]

        holder.workTxt.text = currentItem.ChecklistName
        holder.statusPendingDone.text = currentItem.Status

        if (currentItem.Status == "Pending") {

            Log.d("TAGGGGGGGGGGG", "onBindViewHolder:123 $siteSelect $tourCode $headerSelect $position")
            holder.switchButton.setOnClickListener {
                retrofitInstance.updateChecklistStatustoCompletedHouseKeeping(
                    "sams", siteSelect, tourCode, headerSelect,currentItem.ChecklistAutoID
                ).enqueue(object : Callback<ImageAddingModel?> {
                    override fun onResponse(
                        call: Call<ImageAddingModel?>,
                        response: Response<ImageAddingModel?>
                    ) {
                        if (response.isSuccessful) {

                            if (response.body()?.get(0)?.MessageID?.toInt() == 1) {
                                CustomToast.showToast(context,"Status Updated")

                                holder.statusPendingDone.text = "Completed"
                                holder.switchButton.isEnabled = false
                                holder.switchButton.isChecked = true

                            } else {
                             CustomToast.showToast(context,"Status Update Failed")
                            }

                        } else {
                            CustomToast.showToast(
                                context,
                                "Response not successful"
                            )
                        }
                    }
                    override fun onFailure(call: Call<ImageAddingModel?>, t: Throwable) {
                        CustomToast.showToast(
                            context,
                            "Response failed"
                        )
                    }
                })
            }
        } else {
            holder.switchButton.isEnabled = false
            holder.addPhoto.isClickable= false
            holder.addPhoto.isEnabled  =false

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<CheckListModelItem>) {
        // val diffCallback = DocumentsDiffCallback(checkList, newList)
        // val diffResult = DiffUtil.calculateDiff(diffCallback)

        checkList.clear()
        checkList.addAll(newList)

        notifyDataSetChanged()
    }

    interface Clicked {
        fun onAddPhotoClick(checkListModel: CheckListModelItem, position: Int)
        fun onViewPhotoClick(checkListModel: CheckListModelItem, position: Int)
        //  fun onSwitchOnClick(checkListModel: CheckListModelItem,position: Int)
    }

    fun markStatusToComplete() {

    }

}
