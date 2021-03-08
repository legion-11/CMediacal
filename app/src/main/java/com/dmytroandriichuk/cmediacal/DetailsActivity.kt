package com.dmytroandriichuk.cmediacal

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.data.DataHolder
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import com.dmytroandriichuk.cmediacal.fragments.search.SearchListParentAdapter
import com.dmytroandriichuk.cmediacal.fragments.search.dialog.model.DialogFilterListItem
import com.dmytroandriichuk.cmediacal.viewModel.DetailsViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng
    private lateinit var detailsViewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val clinicData = DataHolder.data
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        title = clinicData?.name
        latLng = LatLng(clinicData!!.lat, clinicData.lng)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //TODO change placeholder
        findViewById<TextView>(R.id.details_clinic_phone).text = "123123123"
        val address = findViewById<TextView>(R.id.details_clinic_address)
        address.text = clinicData.address
        address.setOnClickListener {
            val clip = ClipData.newPlainText("address", address.text)
            clipboard.setPrimaryClip(clip)
        }


        val titles: Array<String> = resources.getStringArray(R.array.filter_titles)
        val filterResources: TypedArray = resources.obtainTypedArray(R.array.filters_resources)
        val filterItems = Array(titles.size) { i ->
            val arrayId = filterResources.getResourceId(i, 0)
            DialogFilterListItem(titles[i], resources.getStringArray(arrayId).toList())
        }
        filterResources.recycle()

        val flattenFilers = filterItems.map { item ->
            Array(item.listOfFilters.size) { i ->
                "${item.title}: ${item.listOfFilters[i]}"
            }
        }.toTypedArray().flatten()
        detailsViewModel = ViewModelProvider(this,
                DetailsViewModel.DetailsViewModelFactory(flattenFilers))
                .get(DetailsViewModel::class.java)

        val mapView: MapView = findViewById(R.id.details_lite_list_row_map)
        with(mapView) {
            onCreate(null)
            getMapAsync(this@DetailsActivity)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.details_clinic_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // obtain full clinic data
        detailsViewModel.searchDoc.observe(this, {
            recyclerView.adapter = DetailsListAdapter(it.servicePrices)
        })
        detailsViewModel.getClinicData(clinicData.id)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(this)
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.addMarker(MarkerOptions().position(latLng).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}