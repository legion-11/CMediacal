package com.dmytroandriichuk.cmediacal.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListChildItem
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListParentItem
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class SearchListParentAdapter(dataSet: ArrayList<SearchListParentItem>,
                              private val context: Context):
    RecyclerView.Adapter<SearchListParentAdapter.ViewHolder>(),
    Filterable {
    private var dataSetFiltered: ArrayList<SearchListParentItem>
    private var dataSetFull = ArrayList(dataSet.sortedWith(compareBy({ it.totalPrice }, { it.name })))
    private val viewPool = RecycledViewPool()

    init {
        dataSetFiltered = ArrayList(dataSetFull)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.search_item_name)
        val addressTV: TextView = view.findViewById(R.id.search_item_address)
        val image: ImageView = view.findViewById(R.id.search_item_clinic_image)
        val totalPrice: TextView = view.findViewById(R.id.search_item_total_price)
        val recyclerView: RecyclerView = view.findViewById(R.id.searchItemPricesRV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_list_parent_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSetFiltered[position]
        holder.nameTV.text = item.name
        holder.addressTV.text = item.address
        Picasso.get().load(item.imageURL).resize(80, 80).centerCrop().into(holder.image)
        holder.totalPrice.text = context.resources.getString(R.string.price_format).format(item.totalPrice)

        val list = item.servicesPrices.map { SearchListChildItem(it.key, it.value) } as ArrayList
        val layoutManager = LinearLayoutManager(holder.recyclerView.context)
        layoutManager.initialPrefetchItemCount = list.size
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = SearchListChildAdapter(list, context)
        holder.recyclerView.setRecycledViewPool(viewPool)
        holder.recyclerView.visibility = if (item.expanded) View.VISIBLE else View.GONE

        holder.view.setOnClickListener {
            item.expanded = !item.expanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = dataSetFiltered.size

    override fun getFilter(): Filter {
        return  object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredListParent: ArrayList<SearchListParentItem> = ArrayList()
                dataSetFiltered = if (constraint.isEmpty()) {
                    dataSetFull
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    for (item in dataSetFull) {
                        if (item.name.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filteredListParent.add(item)
                        }
                    }
                    filteredListParent
                }
                val results = FilterResults()
                results.values = dataSetFiltered
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                dataSetFiltered = results.values as ArrayList<SearchListParentItem>
                notifyDataSetChanged()
            }
        }
    }
}