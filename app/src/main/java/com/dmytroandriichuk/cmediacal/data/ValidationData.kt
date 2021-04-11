package com.dmytroandriichuk.cmediacal.data

import android.graphics.Bitmap

data class ValidationData(
        val id: String, // id of the image
        val clinicListItem: ClinicListItem, // clinic and services that will be validated
        val images: Array<Bitmap> // images for checking
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValidationData

        if (id != other.id) return false
        if (clinicListItem != other.clinicListItem) return false
        if (!images.contentEquals(other.images)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + clinicListItem.hashCode()
        result = 31 * result + images.contentHashCode()
        return result
    }
}