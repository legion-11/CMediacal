package com.dmytroandriichuk.cmediacal.data

import android.graphics.Bitmap

data class ValidationData(
        val id: String,
        val clinicListItem: ClinicListItem,
        val images: Array<Bitmap>
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