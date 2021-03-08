package com.dmytroandriichuk.cmediacal.data

import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ClinicAndServicePrices
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice

data class ClinicListItem(
        val clinic: Clinic,
        val servicePrices: List<ServicePrice>,
        val imageURL: String = "https://therichmonddentalcentre.com/wp-content/uploads/2016/07/IMG_9025.jpg",
        var expanded: Boolean = false,
        var bookmarked: Boolean = false
){
    constructor(clinicAndServicePrices: ClinicAndServicePrices,
                expanded: Boolean = false,
                bookmarked: Boolean = false):
            this(clinicAndServicePrices.clinic, clinicAndServicePrices.prices, expanded = expanded, bookmarked = bookmarked)

    var totalPrice: Double = 0.0
    init {
        for (service in servicePrices){
            totalPrice += service.price
        }
    }
}
