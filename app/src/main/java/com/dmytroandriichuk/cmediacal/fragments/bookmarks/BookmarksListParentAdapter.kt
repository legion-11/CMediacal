package com.dmytroandriichuk.cmediacal.fragments.bookmarks

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.fragments.common.ClinicListChildAdapter
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//first adapter for the recycleView with nested items
class BookmarksListParentAdapter(dataSet: ArrayList<ClinicListItem>,
                                 private val itemClickListener: ItemClickListener,
                                 private val placesClient: PlacesClient):
    RecyclerView.Adapter<BookmarksListParentAdapter.ViewHolder>() {
    private var dataSetFull = ArrayList(dataSet.sortedByDescending { it.clinic.date })
    private lateinit var textFormat: String
    private lateinit var totalPriceHeaderText: String
    private val dateFormat: DateFormat =  SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG)
    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    private val viewPool = RecycledViewPool()


    fun changeDataSet(dataSet: ArrayList<ClinicListItem>){
        dataSetFull = ArrayList(dataSet.sortedByDescending { it.clinic.date })
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
            .inflate(R.layout.clinic_list_parent_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSetFull[position]
        val date = Date(item.clinic.date)
        val datePreviousItem = if (position != 0) Date(dataSetFull[position-1].clinic.date) else Date(0)

        //show date if it is different to previous item
        if (dateFormat.format(datePreviousItem) == dateFormat.format(date)){
            holder.dateTV.visibility = View.GONE
            holder.dividerTop.visibility = View.GONE
        } else {
            holder.dateTV.text = dateFormat.format(date)
            holder.dateTV.visibility = View.VISIBLE
            holder.dividerTop.visibility = if (position != 0) View.VISIBLE else View.GONE
        }
        holder.nameTV.text = item.clinic.name
        holder.addressTV.text = item.clinic.address

        // get image of place by id
        val placeRequest = FetchPlaceRequest.builder(item.clinic.id, listOf(Place.Field.PHOTO_METADATAS)).build()
        placesClient.fetchPlace(placeRequest).addOnSuccessListener { response ->
            val metadata = response.place.photoMetadatas?.first()
            metadata?.let {
                val photoRequest = FetchPhotoRequest.builder(it)
                        .setMaxWidth(100)
                        .setMaxHeight(100)
                        .build()
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                    val bitmap = fetchPhotoResponse.bitmap
                    holder.image.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    holder.image.setImageResource(R.drawable.clinic_default_image)
                }
            }
        }.addOnFailureListener {
            holder.image.setImageResource(R.drawable.clinic_default_image)
        }

        holder.totalPrice.text = if (item.totalPrice != 0.0) textFormat.format(item.totalPrice) else ""
        holder.totalPriceHeader.text = if (item.totalPrice != 0.0) totalPriceHeaderText else ""

        holder.bookmarksButton.isChecked = item.bookmarked
        holder.bookmarksButton.setOnClickListener {
            item.bookmarked = !item.bookmarked
            if (item.bookmarked){
                itemClickListener.addBookmark(dataSetFull[position])
            } else {
                itemClickListener.removeBookmark(dataSetFull[position])
            }
        }

        holder.infoButton.setOnClickListener {
            itemClickListener.itemClicked(item)
        }

        holder.latLng = LatLng(item.clinic.lat, item.clinic.lng)
        holder.setMapLocation()
        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
        val list = item.servicePrices
        if (list.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(holder.recyclerView.context)
            layoutManager.initialPrefetchItemCount = list.size
            holder.recyclerView.layoutManager = layoutManager

            holder.recyclerView.adapter = ClinicListChildAdapter(list)
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
    override fun getItemCount(): Int = dataSetFull.size


    override fun onViewRecycled(holder: ViewHolder) {
        holder.clearView()
        super.onViewRecycled(holder)
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
        val dateTV: TextView = view.findViewById(R.id.date)
        val recyclerView: RecyclerView = view.findViewById(R.id.search_item_prices_rv)
        val mapView: MapView = view.findViewById(R.id.search_lite_list_row_map)
        val dividerTop: View = view.findViewById(R.id.dividerTop)

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
            if (!::map.isInitialized) return
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
        fun addBookmark(item: ClinicListItem)
        fun removeBookmark(item: ClinicListItem)
        fun itemClicked(item: ClinicListItem)
    }
}