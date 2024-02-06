package com.example.ifmapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.AdapterView
import android.widget.Spinner
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference

class CustomSpinnerAdapter(
    private val context: Context,
    private val data: List<String>,
    private var userId: String,
    private val spinner: Spinner, // Pass the Spinner to close it programmatically
    private val onItemSelectedListener: OnItemSelectedListener
) : BaseAdapter() {

    private var selectedPosition: Int = -1 // Track the selected position
    private var selectedItemId: Long = -1 // Track the selected item id

    interface OnItemSelectedListener {
        fun onItemSelected(item: String)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false)
            holder = ViewHolder()
            holder.textView = view.findViewById(android.R.id.text1)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = getItem(position) as String
        holder.textView.text = item

        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
            holder = ViewHolder()
            holder.textView = view.findViewById(android.R.id.text1)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val originalItem = getItem(position) as String
        val modifiedItem = modifyItem(originalItem) // Your custom modification logic

        holder.textView.text = modifiedItem

        view?.setOnClickListener {
            onItemSelectedListener.onItemSelected(originalItem)
            selectedPosition = position
            selectedItemId = getItemId(position)
            spinner.setSelection(selectedPosition)
            spinner.performClick()
        }

        return view!!
    }

    private fun modifyItem(originalItem: String): String {
        for (sites in SaveUsersInSharedPreference.getCurrentUserShifts(context, userId)) {
            if (sites.site == originalItem) {
                return sites.site
            }
        }
        return originalItem
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun getSelectedItemId(): Long {
        return selectedItemId
    }

    private class ViewHolder {
        lateinit var textView: TextView
    }
}
