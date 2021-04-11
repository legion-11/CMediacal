package com.dmytroandriichuk.cmediacal.adapter.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
/* adapter for recyclerView that shows all services and its price for selected clinic */
class DetailsServicesListAdapter(private val dataSet: List<ServicePrice>, private val itemPressListener: ItemPressListener): RecyclerView.Adapter<DetailsServicesListAdapter.ViewHolder>() {

    private lateinit var textFormat: String
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        textFormat = recyclerView.context.resources.getString(R.string.price_format)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_service_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.nameTV.text = item.serviceName
        holder.priceTV.text = textFormat.format(item.price)

        holder.view.setOnClickListener {
            itemPressListener.getImages("tag ${item.serviceName}")
        }
    }

    override fun getItemCount(): Int = dataSet.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.detail_item_service_name)
        val priceTV: TextView = view.findViewById(R.id.detail_item_service_price)
    }


    interface ItemPressListener {
        fun getImages(tag: String)
    }
}