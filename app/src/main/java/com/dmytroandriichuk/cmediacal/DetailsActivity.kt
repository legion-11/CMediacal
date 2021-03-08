package com.dmytroandriichuk.cmediacal

import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.dmytroandriichuk.cmediacal.data.DataHolder
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

        // obtain full cliinic data
        detailsViewModel.searchDoc.observe(this, {
            Log.d("DetailsActivity", "onCreate: $it")
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