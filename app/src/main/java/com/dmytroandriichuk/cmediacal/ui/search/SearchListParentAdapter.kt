package com.dmytroandriichuk.cmediacal.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListChildItem
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListParentItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

//first adapter for the recycleView with nested items
class SearchListParentAdapter(dataSet: ArrayList<SearchListParentItem>, private val itemClickListener: ItemClickListener):
    RecyclerView.Adapter<SearchListParentAdapter.ViewHolder>(),
    Filterable {
    private var dataSetFiltered: ArrayList<SearchListParentItem>
    private var dataSetFull = ArrayList(dataSet.sortedWith(compareBy({ it.totalPrice }, { it.name })))
    private lateinit var textFormat: String
    private lateinit var totalPriceHeaderText: String
    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    private val viewPool = RecycledViewPool()

    init {
        //copy the list for future filtering
        dataSetFiltered = ArrayList(dataSetFull)
    }

    fun changeDataSet(dataSet: ArrayList<SearchListParentItem>){
        dataSetFull = ArrayList(dataSet.sortedWith(compareBy({ it.totalPrice }, { it.name })))
        dataSetFiltered = ArrayList(dataSetFull)
        notifyDataSetChanged()
        Log.d("TAG", "changeDataSet: $dataSetFull")
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        textFormat = recyclerView.context.resources.getString(R.string.price_format)
        totalPriceHeaderText = recyclerView.context.resources.getString(R.string.total_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Here we inflate the corresponding
        // layout of the parent item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_list_parent_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSetFiltered[position]
        holder.nameTV.text = item.name
        holder.addressTV.text = item.address
        Picasso.get().load(item.imageURL).resize(80, 80).centerCrop().into(holder.image)
        holder.totalPrice.text = if (item.totalPrice != 0.0) textFormat.format(item.totalPrice) else ""
        holder.totalPriceHeader.text = if (item.totalPrice != 0.0) totalPriceHeaderText else ""

        holder.bookmarksButton.isChecked = item.bookmarked
        holder.bookmarksButton.setOnClickListener {
            item.bookmarked = !item.bookmarked
            if (item.bookmarked){
                itemClickListener.addBookmark(dataSetFiltered[position])
            } else {
                itemClickListener.removeBookmark(dataSetFiltered[position])
            }
        }

        holder.infoButton.setOnClickListener {
            itemClickListener.itemClicked(item)
        }

        holder.latLng = LatLng(item.lat, item.lng)
        holder.setMapLocation()
        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
        val list = item.servicesPrices.map { SearchListChildItem(it.key, it.value) } as ArrayList
        if (list.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(holder.recyclerView.context)
            layoutManager.initialPrefetchItemCount = list.size
            holder.recyclerView.layoutManager = layoutManager

            holder.recyclerView.adapter = SearchListChildAdapter(list)
            holder.recyclerView.setRecycledViewPool(viewPool)
            holder.recyclerView.visibility = if (item.expanded) View.VISIBLE else View.GONE
        } else {
            holder.recyclerView.visibility = View.GONE
        }

        holder.mapView.visibility = if (item.expanded) View.VISIBLE else View.GONE
        //expand item on click
        holder.view.setOnClickListener {
            item.expanded = !item.expanded
            notifyItemChanged(position)
        }
    }

    //number of items in recycleView
    override fun getItemCount(): Int = dataSetFiltered.size

    //filter items by clinic name
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

    override fun onViewRecycled(holder: ViewHolder) {
        holder.clearView()
        super.onViewRecycled(holder)
        Log.d("SearchListParentAdapter", "onViewRecycled: ")
    }

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        OnMapReadyCallback {
        val nameTV: TextView = view.findViewById(R.id.search_item_name_tv)
        val addressTV: TextView = view.findViewById(R.id.search_item_address_tv)
        val image: ImageView = view.findViewById(R.id.search_item_clinic_image)
        val totalPriceHeader: TextView = view.findViewById(R.id.search_item_total_text_tv)
        val totalPrice: TextView = view.findViewById(R.id.search_item_total_price_tv)
        val bookmarksButton: ToggleButton = view.findViewById(R.id.search_item_bookmarks_tglBtn)
        val infoButton: ImageButton = view.findViewById(R.id.search_item_info_btn)
        val recyclerView: RecyclerView = view.findViewById(R.id.search_item_prices_rv)
        val mapView: MapView = view.findViewById(R.id.search_lite_list_row_map)

        lateinit var latLng: LatLng
        private lateinit var map: GoogleMap

        init {
            mapView.isClickable = false
            with(mapView) {
                // Initialise the MapView
                onCreate(null)
                // Set the map ready callback to receive the GoogleMap object
                getMapAsync(this@ViewHolder)
            }
        }

        fun setMapLocation() {
            if (!::map.isInitialized) return
            with(map) {
                addMarker(MarkerOptions().position(latLng))
                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        /** This function is called when we need to clear the map. */
        fun clearView() {
            with(map) {
                // Clear the map and free up resources by changing the map type to none
                clear()
                mapType = GoogleMap.MAP_TYPE_NONE
                Log.d("ViewHolder", "clearView: map cleared")
            }
        }

        override fun onMapReady(googleMap: GoogleMap?) {
            MapsInitializer.initialize(recyclerView.context)
            // If map is not initialised properly
            map = googleMap ?: return
            setMapLocation()
        }
    }

    interface ItemClickListener {
        fun addBookmark(item: SearchListParentItem)
        fun removeBookmark(item: SearchListParentItem)
        fun itemClicked(item: SearchListParentItem)
    }
}