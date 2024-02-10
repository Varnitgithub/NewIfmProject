import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.R
import com.example.ifmapp.activities.checklists.CheckListModel
import com.example.ifmapp.activities.checklists.housekeeping_model.ViewPhotoResponseItem
import com.example.ifmapp.modelclasses.SalaryModel

class ViewPhotoHouseKeepingAdapter(private val context: Context) :
    RecyclerView.Adapter<ViewPhotoHouseKeepingAdapter.DocumentsViewHolder>() {

    var checkList = ArrayList<ViewPhotoResponseItem>()

    class DocumentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.houseKeepingImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        val view =
            DocumentsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_housekeepingphoto_item, parent, false)
            )




        return view
    }

    override fun getItemCount(): Int {
        return checkList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
        val currentItem = checkList[position]
        val bitmapImage = base64ToBitmap(currentItem.ImageBase64String)
        if (bitmapImage != null) {
            holder.image.setImageBitmap(bitmapImage)
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<ViewPhotoResponseItem>) {


        checkList.clear()
        checkList.addAll(newList)
        notifyDataSetChanged()
    }

    fun base64ToBitmap(base64Image: String): Bitmap? {
        try {
            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            // Handle exceptions, e.g., IllegalArgumentException or OutOfMemoryError
            e.printStackTrace()
        }
        return null
    }

}
