package com.dmytroandriichuk.cmediacal.ui.search.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.dialog.model.DialogFilterListItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

//adapter for recycleView for search clinics
class FilterListAdapter(private val dataSet: List<DialogFilterListItem>,
                        private val context: Context,
                        private val listener: FilterListDialog.FilterListDialogListener):
    RecyclerView.Adapter<FilterListAdapter.ViewHolder>() {
      class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTV: TextView = view.findViewById(R.id.filters_item_title)
        val chipGroup: ChipGroup = view.findViewById(R.id.filters_item_chip_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_dialog_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.titleTV.text = item.title
        for (filter in item.listOfFilters) {
            val chip = Chip(context)
            chip.text = filter
            chip.isCheckable = true
            //add padding so chips would not change their size on click due to icon appearance
            chip.chipStartPadding = 37f
            chip.chipEndPadding = 37f
//            chip.setChipBackgroundColorResource(R.color.colorAccent)
//            chip.setTextColor(getResources().getColor(R.color.white))
//            chip.setTextAppearance(R.style.ChipTextAppearance)

            //
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    listener.setFilter(item.title + " : " + buttonView.text.toString())
                    chip.chipStartPadding = 8f
                    chip.chipEndPadding = 0f
                } else {
                    listener.removeFilter(item.title + " : " + buttonView.text.toString())
                    chip.chipStartPadding = 37f
                    chip.chipEndPadding = 37f
                }
            }
            holder.chipGroup.addView(chip)
        }
    }
    override fun getItemCount(): Int = dataSet.size

}