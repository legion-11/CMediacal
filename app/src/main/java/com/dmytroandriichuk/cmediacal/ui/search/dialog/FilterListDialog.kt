package com.dmytroandriichuk.cmediacal.ui.search.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.dialog.model.DialogFilterListItem

//Dialog that provides user with filtering options for recycleView
class FilterListDialog(private val listener: FilterListDialogListener): AppCompatDialogFragment(){

    private lateinit var recyclerView: RecyclerView

    //interface for filter click callback
    interface FilterListDialogListener {
        fun startFiltering()
        fun setFilter(filter: String)
        fun removeFilter(filter: String)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_filter_list, null)
            builder.setView(view)

            //each element of recycleView is title with chip group
            recyclerView = view as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val titles: Array<String> = resources.getStringArray(R.array.filter_titles)
            val filterResources: TypedArray = resources.obtainTypedArray(R.array.filters_resources)
            val filterItems = List(titles.size) { i ->
                val arrayId = filterResources.getResourceId(i, 0)
                DialogFilterListItem(titles[i], resources.getStringArray(arrayId).toList())
            }

            filterResources.recycle()
            recyclerView.adapter = FilterListAdapter(filterItems, recyclerView.context, listener)
            builder.setPositiveButton("Apply") {
                        dialog, _ ->  listener.startFiltering()
                    }
            builder.setNegativeButton("Close") {
                dialog, _ ->  dialog.dismiss()
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}