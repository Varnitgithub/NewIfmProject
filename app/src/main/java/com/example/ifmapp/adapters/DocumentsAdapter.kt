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

class DocumentsAdapter(private var context: Context):RecyclerView.Adapter<DocumentsAdapter.DocumentsViewHolder>() {

    var documentsList=ArrayList<DocumentsModel>()

    class DocumentsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var docImage:ImageView = itemView.findViewById(R.id.doc_image)
        var docName:TextView = itemView.findViewById(R.id.doc_name)
        var docNumber:TextView = itemView.findViewById(R.id.doc_no)
        var doc_LL:LinearLayout = itemView.findViewById(R.id.doc_LL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.document_items,parent,false)

       return DocumentsViewHolder(view)
    }

    override fun getItemCount(): Int {
      return documentsList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
     val currentItem = documentsList[position]

        holder.docName.text = currentItem.doc_name
        holder.docNumber.text = currentItem.doc_no
       // Glide.with(context).load(currentItem.doc_image).placeholder(R.drawable.aadhar).into(holder.docImage)
    }
    fun updateList(newList: List<DocumentsModel>) {
        val diffCallback = DocumentsDiffCallback(documentsList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        documentsList.clear()
        documentsList.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    class DocumentsDiffCallback(
        private val oldList: List<DocumentsModel>,
        private val newList: List<DocumentsModel>
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