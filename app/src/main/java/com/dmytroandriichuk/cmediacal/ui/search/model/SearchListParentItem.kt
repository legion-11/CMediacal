package com.dmytroandriichuk.cmediacal.ui.search.model

import android.media.Image
import com.dmytroandriichuk.cmediacal.R

//POJO item of SearchListParentAdapter
data class SearchListParentItem(
        var name: String = "Placeholder Clinic",
        var address: String = "Placeholder Address",
        var imageURL: String = "https://therichmonddentalcentre.com/wp-content/uploads/2016/07/IMG_9025.jpg",
        var servicesPrices: Map<String, Double> = mapOf(
                "Filling: small" to 150.0,
                "Other: cleaning" to 100.0,
                "X-ray" to 150.0
        ),
        var expanded: Boolean = false
) {
    var totalPrice: Double = 0.0
    init {
        calcTotal()
    }

    fun updateServicesPrices(value: Map<String, Double>){
        servicesPrices = value
        calcTotal()
    }

    fun calcTotal(){
        totalPrice = 0.0
        for (s in servicesPrices){
            totalPrice += s.value
        }
    }

    override fun toString(): String {
        return "SearchListItem(name='$name', address='$address', imageURL='$imageURL', servicesPrices=$servicesPrices, totalPrice=$totalPrice)"
    }
}