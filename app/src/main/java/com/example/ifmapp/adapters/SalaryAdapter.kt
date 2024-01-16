package com.example.ifmapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ifmapp.R
import com.example.ifmapp.modelclasses.DocumentsModel
import com.example.ifmapp.modelclasses.SalaryModel

class SalaryAdapter(private var context: Context):RecyclerView.Adapter<SalaryAdapter.DocumentsViewHolder>() {

    var documentsList=ArrayList<SalaryModel>()

    class DocumentsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var salaryDate:TextView = itemView.findViewById(R.id.salary_time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.salary_screen_items,parent,false)

       return DocumentsViewHolder(view)
    }

    override fun getItemCount(): Int {
      return documentsList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
     val currentItem = documentsList[position]

        holder.salaryDate.text = currentItem.salaryDate
       // Glide.with(context).load(currentItem.doc_image).placeholder(R.drawable.aadhar).into(holder.docImage)
    }
    fun updateList(newList: List<SalaryModel>) {
        val diffCallback = DocumentsDiffCallback(documentsList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        documentsList.clear()
        documentsList.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    class DocumentsDiffCallback(
        private val oldList: List<SalaryModel>,
        private val newList: List<SalaryModel>
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
}