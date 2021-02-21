package com.dmytroandriichuk.cmediacal.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListChildItem

class SearchListChildAdapter(private val dataSet: ArrayList<SearchListChildItem>, private val context: Context): RecyclerView.Adapter<SearchListChildAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.search_item_service_name)
        val priceTV: TextView = view.findViewById(R.id.search_item_service_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_list_child_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.nameTV.text = item.name
        holder.priceTV.text = context.resources.getString(R.string.price_format).format(item.price)
    }

    override fun getItemCount(): Int = dataSet.size
}