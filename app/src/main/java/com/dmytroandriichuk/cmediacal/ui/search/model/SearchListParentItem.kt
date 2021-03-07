package com.dmytroandriichuk.cmediacal.ui.search.model

import com.google.android.gms.maps.model.LatLng

//POJO item of SearchListParentAdapter
data class SearchListParentItem(
    val id: String,
    var name: String = "Placeholder Clinic",
    var address: String = "Placeholder Address",
    var imageURL: String = "https://therichmonddentalcentre.com/wp-content/uploads/2016/07/IMG_9025.jpg",
    var servicesPrices: Map<String, Double> = mapOf(),
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var expanded: Boolean = false,
    var bookmarked: Boolean = false
) {
    var totalPrice: Double = 0.0
    init {
        calculateTotal()
    }

    fun updateServicesPrices(value: Map<String, Double>){
        servicesPrices = value
        calculateTotal()
    }

    private fun calculateTotal(){
        totalPrice = 0.0
        for (s in servicesPrices){
            totalPrice += s.value
        }
    }

    override fun toString(): String {
        return "SearchListItem(name='$name', address='$address', imageURL='$imageURL', servicesPrices=$servicesPrices, totalPrice=$totalPrice)"
    }
}