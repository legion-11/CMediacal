package com.dmytroandriichuk.cmediacal.ui.search.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.TypedArray
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.dialog.dataClasses.FilterListParentItem
import com.google.protobuf.Empty

class FilterListDialog: AppCompatDialogFragment(){

    private lateinit var recyclerView: RecyclerView

    interface FilterListDialogListener {
        fun startFiltering()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_filter_list, null)
            builder.setView(view)

            recyclerView = view as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.addItemDecoration(
                    DividerItemDecoration(
                            recyclerView.context,
                            DividerItemDecoration.VERTICAL
                    )
            )
            val titles: Array<String> = resources.getStringArray(R.array.filter_titles)
            val filterResources: TypedArray = resources.obtainTypedArray(R.array.filters_resources)
            val filterItems = List(titles.size) { i ->
                val arrayId = filterResources.getResourceId(i, 0)
                FilterListParentItem(titles[i], resources.getStringArray(arrayId).toList())
            }
            filterResources.recycle()
            recyclerView.adapter = FilterListAdapter(filterItems, recyclerView.context)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}