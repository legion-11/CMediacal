package com.dmytroandriichuk.cmediacal.ui.search.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.dialog.dataClasses.FilterListParentItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class FilterListAdapter(private val dataSet: List<FilterListParentItem>, private val context: Context): RecyclerView.Adapter<FilterListAdapter.ViewHolder>() {
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
//            chip.isCloseIconVisible = true
//            chip.setTextColor(getResources().getColor(R.color.white))
//            chip.setTextAppearance(R.style.ChipTextAppearance)
            holder.chipGroup.addView(chip)
        }

    }

    override fun getItemCount(): Int = dataSet.size

}