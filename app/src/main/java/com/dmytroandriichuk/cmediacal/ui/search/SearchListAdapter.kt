package com.dmytroandriichuk.cmediacal.ui.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListItem
import com.squareup.picasso.Picasso
import android.widget.Filter
import android.widget.Filterable
import com.dmytroandriichuk.cmediacal.ui.search.SearchFragment.Companion.TAG
import java.util.*
import kotlin.collections.ArrayList

class SearchListAdapter(private var dataSet: List<SearchListItem>,
                        private val context: Context):
    RecyclerView.Adapter<SearchListAdapter.ViewHolder>(),
    Filterable {

    private val dataSetFull: List<SearchListItem>

    init {
        dataSet = dataSet.sortedWith(compareBy<SearchListItem>{ it.totalPrice }.thenBy { it.name })
        dataSetFull = ArrayList(dataSet)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.search_item_name)
        val addressTV: TextView = view.findViewById(R.id.search_item_address)
        val image: ImageView = view.findViewById(R.id.search_item_clinic_image)
        val totalPrice: TextView = view.findViewById(R.id.search_item_total_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.nameTV.text = item.name
        holder.addressTV.text = item.address
        Picasso.get().load(item.imageURL).resize(80, 80).centerCrop().into(holder.image)
        holder.totalPrice.text = context.resources.getString(R.string.price_format).format(item.totalPrice)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getFilter(): Filter {
        return  object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredList: MutableList<SearchListItem> = ArrayList()
                if (constraint.isEmpty()) {
                    filteredList.addAll(dataSetFull)
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    for (item in dataSetFull) {
                        if (item.name.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                (dataSet as MutableList).clear()
                (dataSet as MutableList).addAll(results.values as List<SearchListItem>)
                dataSet = dataSet.sortedWith(compareBy({ it.totalPrice }, { it.name }))
                notifyDataSetChanged()
            }
        }
    }
}