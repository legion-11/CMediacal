package com.dmytroandriichuk.cmediacal.ui.search

import android.content.Context
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
import java.util.*

class SearchListAdapter(dataSet: ArrayList<SearchListItem>,
                        private val context: Context):
    RecyclerView.Adapter<SearchListAdapter.ViewHolder>(),
    Filterable {
    private var dataSetFiltered: ArrayList<SearchListItem>
    private var dataSetFull = ArrayList(dataSet.sortedWith(compareBy({ it.totalPrice }, { it.name })))

    init {
        dataSetFiltered = ArrayList(dataSetFull)
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
        val item = dataSetFiltered[position]
        holder.nameTV.text = item.name
        holder.addressTV.text = item.address
        Picasso.get().load(item.imageURL).resize(80, 80).centerCrop().into(holder.image)
        holder.totalPrice.text = context.resources.getString(R.string.price_format).format(item.totalPrice)
    }

    override fun getItemCount(): Int = dataSetFiltered.size

    override fun getFilter(): Filter {
        return  object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredList: ArrayList<SearchListItem> = ArrayList()
                dataSetFiltered = if (constraint.isEmpty()) {
                    dataSetFull
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    for (item in dataSetFiltered) {
                        if (item.name.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                    filteredList
                }
                val results = FilterResults()
                results.values = dataSetFiltered
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                dataSetFiltered = results.values as ArrayList<SearchListItem>
                notifyDataSetChanged()
            }
        }
    }
}