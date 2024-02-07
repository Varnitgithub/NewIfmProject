package com.example.ifmapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ifmapp.R
import com.example.ifmapp.modelclasses.DocumentsModel

class DocumentsAdapter(private var context: Context,private var listener:Clicked):RecyclerView.Adapter<DocumentsAdapter.DocumentsViewHolder>() {

    var documentsList=ArrayList<DocumentsModel>()

    class DocumentsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var docImage:ImageView = itemView.findViewById(R.id.doc_image)
        var docName:TextView = itemView.findViewById(R.id.doc_name)
       // var docNumber:TextView = itemView.findViewById(R.id.doc_no)
        var doc_Card:CardView = itemView.findViewById(R.id.document_cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        var view = DocumentsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.document_items,parent,false)
        )
        view.doc_Card.setOnClickListener {
            listener.onclick(documentsList,view.adapterPosition)
        }
       return view
    }

    override fun getItemCount(): Int {
      return documentsList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
     val currentItem = documentsList[position]

        holder.docName.text = currentItem.doc_name
        Glide.with(context).load(currentItem.doc_image).placeholder(R.drawable.aadhar).into(holder.docImage)
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
    interface Clicked{
        fun onclick(model: ArrayList<DocumentsModel>,position: Int)
    }
}