package com.dmytroandriichuk.cmediacal.ui.search.dialog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.dialog.OfflineDialog
import com.dmytroandriichuk.cmediacal.ui.search.SearchFragment.Companion.TAG
import com.dmytroandriichuk.cmediacal.ui.search.dialog.dataClasses.FilterListParentItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.lang.ClassCastException


class FilterListAdapter(private val dataSet: List<FilterListParentItem>, private val context: Context, private val listener: ChipClickListener): RecyclerView.Adapter<FilterListAdapter.ViewHolder>() {
    interface ChipClickListener {
        fun setFilter(filter: String)
        fun removeFilter(filter: String)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.filters_item_title)
        val chipGroup: ChipGroup = view.findViewById(R.id.filters_item_chip_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.titleTV.text = item.title
        for (filter in item.listOfFilters) {
            val chip = Chip(context)
            chip.text = filter
            chip.isCheckable = true
//            chip.setChipBackgroundColorResource(R.color.colorAccent)
//            chip.setTextColor(getResources().getColor(R.color.white))
//            chip.setTextAppearance(R.style.ChipTextAppearance)
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    listener.setFilter(buttonView.text.toString())
                } else {
                    listener.removeFilter(buttonView.text.toString())
                }
            }
            holder.chipGroup.addView(chip)
        }
    }
    override fun getItemCount(): Int = dataSet.size

}