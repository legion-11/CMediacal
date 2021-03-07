package com.dmytroandriichuk.cmediacal.db.entity

import androidx.room.Embedded
import androidx.room.Relation


//data class for cross referencing
data class ClinicAndServicePrices (
    @Embedded val clinic: Clinic,
    @Relation(
        parentColumn = "crossRefId",
        entityColumn = "crossRefServiceId",
    )
    val prices: List<ServicePrice>
){
    override fun toString(): String {
        return "ClinicAndServicePrices(clinic=$clinic, prices=$prices)"
    }
}