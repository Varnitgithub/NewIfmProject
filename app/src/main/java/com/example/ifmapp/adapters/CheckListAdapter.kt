import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.R
import com.example.ifmapp.activities.checklists.CheckListModel
import com.example.ifmapp.modelclasses.SalaryModel

class CheckListAdapter(private val context: Context,private var listener:Clicked) :
    RecyclerView.Adapter<CheckListAdapter.DocumentsViewHolder>() {

    var checkList = ArrayList<CheckListModel>()

    class DocumentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workTxt: TextView = itemView.findViewById(R.id.work_txt)
        var statusPendingDone: TextView = itemView.findViewById(R.id.statusDone_pending)
        var addPhoto:Button = itemView.findViewById(R.id.btn_addphoto)
        var viewPhoto:Button = itemView.findViewById(R.id.btn_viewPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        val view =
            DocumentsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.checklist_for_housekeeping_item, parent, false)
            )

        view.addPhoto.setOnClickListener {
            listener.onAddPhotoClick(checkList[view.adapterPosition],view.adapterPosition)
        }
        view.viewPhoto.setOnClickListener {
            listener.onViewPhotoClick(checkList[view.adapterPosition],view.adapterPosition)
        }
        view.statusPendingDone.setOnClickListener {
            listener.onMarkStatusClick(checkList[view.adapterPosition],view.adapterPosition)
        }
        return view
    }

    override fun getItemCount(): Int {
        return checkList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
        val currentItem = checkList[position]

        holder.workTxt.text = currentItem.workText
        holder.statusPendingDone.text = currentItem.status
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<CheckListModel>) {
        val diffCallback = DocumentsDiffCallback(checkList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        checkList.clear()
        checkList.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    class DocumentsDiffCallback(
        private val oldList: List<CheckListModel>,
        private val newList: List<CheckListModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
    interface Clicked{
        fun onAddPhotoClick(checkListModel: CheckListModel,position: Int)
        fun onViewPhotoClick(checkListModel: CheckListModel,position: Int)
        fun onMarkStatusClick(checkListModel: CheckListModel,position: Int)
    }
}
