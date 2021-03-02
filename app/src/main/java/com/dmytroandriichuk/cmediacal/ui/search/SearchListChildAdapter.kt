package com.dmytroandriichuk.cmediacal.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListChildItem

//adapter for nester recycleView
class SearchListChildAdapter(private val dataSet: ArrayList<SearchListChildItem>): RecyclerView.Adapter<SearchListChildAdapter.ViewHolder>() {

    private lateinit var textFormat: String
    // This class is to initialize
    // the Views present
    // in the child RecyclerView
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.search_item_service_name)
        val priceTV: TextView = view.findViewById(R.id.search_item_service_price)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        textFormat = recyclerView.context.resources.getString(R.string.price_format)
    }

    // Here we inflate the corresponding
    // layout of the child item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_list_child_item, parent, false)
        return ViewHolder(view)
    }

    // Create an instance of the ChildItem
    // class for the given position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.nameTV.text = item.name
        holder.priceTV.text = textFormat.format(item.price)
    }

    override fun getItemCount(): Int = dataSet.size
}