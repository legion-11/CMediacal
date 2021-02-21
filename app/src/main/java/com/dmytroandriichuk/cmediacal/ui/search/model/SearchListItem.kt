package com.dmytroandriichuk.cmediacal.ui.search.model

import android.media.Image
import com.dmytroandriichuk.cmediacal.R

data class SearchListItem(
        val name: String = "Placeholder Clinic",
        val address: String = "222 Toronto Street",
        val imageURL: String = "https://therichmonddentalcentre.com/wp-content/uploads/2016/07/IMG_9025.jpg",
        val totalPrice: Int = 500
) {
    override fun toString(): String {
        return "Clinic(name='$name', address='$address', imageURL=$imageURL, totalPrice=$totalPrice)"
    }
}