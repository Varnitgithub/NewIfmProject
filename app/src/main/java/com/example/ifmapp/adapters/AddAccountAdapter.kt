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
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem

class AddAccountAdapter(private var context: Context,private var listener:OnClickedInterface):RecyclerView.Adapter<AddAccountAdapter.DocumentsViewHolder>() {

    private var documentsList=ArrayList<LoginByPINResponseItem>()

    class DocumentsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var addAccountImage:ImageView = itemView.findViewById(R.id.addAccount_imageview)
        var addAccountName:TextView = itemView.findViewById(R.id.add_accountName)
        var addAccountLL:LinearLayout = itemView.findViewById(R.id.addAccountLLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsViewHolder {
        val view = DocumentsViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.add_account_layout,parent,false))


        view.addAccountLL.setOnClickListener {
            listener.onclick(documentsList[view.adapterPosition],view.adapterPosition)
        }
       return view
    }

    override fun getItemCount(): Int {
      return documentsList.size
    }

    override fun onBindViewHolder(holder: DocumentsViewHolder, position: Int) {
     val currentItem = documentsList[position]

        holder.addAccountName.text = currentItem.EmpName

        Glide.with(context).load(R.drawable.account_user_profile).placeholder(R.drawable.aadhar).into(holder.addAccountImage)
    }
    fun updateList(newList: List<LoginByPINResponseItem>) {
        val diffCallback = DocumentsDiffCallback(documentsList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        documentsList.clear()
        documentsList.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    class DocumentsDiffCallback(
        private val oldList: List<LoginByPINResponseItem>,
        private val newList: List<LoginByPINResponseItem>
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

    interface OnClickedInterface{
       fun onclick(employeeModel:LoginByPINResponseItem,position: Int)
    }
}