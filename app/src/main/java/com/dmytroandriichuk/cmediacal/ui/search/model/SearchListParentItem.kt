package com.dmytroandriichuk.cmediacal.ui.search.model

import android.media.Image
import com.dmytroandriichuk.cmediacal.R

//POJO item of SearchListParentAdapter
data class SearchListParentItem(
        val name: String = "Placeholder Clinic",
        val address: String = "222 Toronto Street",
        val imageURL: String = "https://therichmonddentalcentre.com/wp-content/uploads/2016/07/IMG_9025.jpg",
        val servicesPrices: HashMap<String, Float> = hashMapOf(
                "Filling: small" to 150f,
                "Other: cleaning" to 100f,
                "X-ray" to 150f
        ),
        var expanded: Boolean = false
) {
    var totalPrice: Float = 0f
    init {
        for (s in servicesPrices){
            totalPrice += s.value
        }
    }

    override fun toString(): String {
        return "SearchListItem(name='$name', address='$address', imageURL='$imageURL', servicesPrices=$servicesPrices, totalPrice=$totalPrice)"
    }
}