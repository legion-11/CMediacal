package com.dmytroandriichuk.cmediacal.fragments.search.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.fragments.search.dialog.model.DialogFilterListItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

//adapter for recycleView for search clinics
class FilterListDialogAdapter(private val dataSet: List<DialogFilterListItem>,
                              private val listener: FilterListDialog.FilterListDialogListener):
    RecyclerView.Adapter<FilterListDialogAdapter.ViewHolder>() {

    lateinit var context: Context
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.filters_item_title)
        val chipGroup: ChipGroup = view.findViewById(R.id.filters_item_chip_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_filter_dialog_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.titleTV.text = item.title
        for (filter in item.listOfFilters) {
            val chip = Chip(context)
            chip.text = filter
            chip.isCheckable = true

            if (filter in listener.provinces ||  "${item.title}: $filter"  in listener.filters){
                chip.isChecked = true
                chip.chipStartPadding = 8f
                chip.chipEndPadding = 0f

            } else {
                chip.chipStartPadding = 37f
                chip.chipEndPadding = 37f
            }

            //add padding so chips would not change their size on click due to icon appearance

            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (position == 0) {
                    if (isChecked) {
                        if (listener.provinces.size <= 9){
                            listener.setProvince(filter)
                        } else {
                            buttonView.isChecked = false
                        }
                    } else {
                        listener.removeProvince(filter)
                    }
                } else {
                    if (isChecked) {
                        listener.setFilter("${item.title}: $filter")
                    } else {
                        listener.removeFilter("${item.title}: $filter")
                    }
                }
                changeChipPadding(chip)
            }
            holder.chipGroup.addView(chip)
        }
    }
    override fun getItemCount(): Int = dataSet.size

    private fun changeChipPadding(chip: Chip) {
        chip.chipStartPadding = if (chip.isChecked) 8f else 37f
        chip.chipEndPadding = if (chip.isChecked) 0f else 37f
    }
}