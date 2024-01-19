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
import com.bumptech.glide.Glide
import com.example.ifmapp.R
import com.example.ifmapp.modelclasses.AddAccountModel
import com.example.ifmapp.modelclasses.DocumentsModel

class AddAccountAdapter(private var context: Context):RecyclerView.Adapter<AddAccountAdapter.DocumentsViewHolder>() {

    var documentsList=ArrayList<AddAccountModel>()

    class DocumentsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var addAccountImage:ImageView = itemView.findViewById(R.id.addAccount_imageview)
        var addAccountName:TextView = itemView.findViewById(R.id.add_accountName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.add_account_layout,parent,false)

       return DocumentsViewHolder(view)
    }

    override fun getItemCount(): Int {
      return documentsList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
     val currentItem = documentsList[position]

        holder.addAccountName.text = currentItem.name

        Glide.with(context).load(currentItem.image).placeholder(R.drawable.aadhar).into(holder.addAccountImage)
    }
    fun updateList(newList: List<AddAccountModel>) {
        val diffCallback = DocumentsDiffCallback(documentsList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        documentsList.clear()
        documentsList.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    class DocumentsDiffCallback(
        private val oldList: List<AddAccountModel>,
        private val newList: List<AddAccountModel>
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