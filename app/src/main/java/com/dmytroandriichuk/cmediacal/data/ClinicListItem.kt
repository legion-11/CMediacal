package com.dmytroandriichuk.cmediacal.data

import android.os.Parcel
import android.os.Parcelable
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ClinicAndServicePrices
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice

data class ClinicListItem(
        val clinic: Clinic,
        val servicePrices: List<ServicePrice>,
        val imageURL: String = "https://therichmonddentalcentre.com/wp-content/uploads/2016/07/IMG_9025.jpg",
        var expanded: Boolean = false,
        var bookmarked: Boolean = false
): Parcelable {
    constructor(clinicAndServicePrices: ClinicAndServicePrices,
                expanded: Boolean = false,
                bookmarked: Boolean = false):
            this(clinicAndServicePrices.clinic, clinicAndServicePrices.prices, expanded = expanded, bookmarked = bookmarked)

    var totalPrice: Double = 0.0

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Clinic::class.java.classLoader)!!,
            parcel.createTypedArrayList(ServicePrice)!!,
            parcel.readString()!!,
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte()) {
        totalPrice = parcel.readDouble()
    }

    init {
        for (service in servicePrices){
            totalPrice += service.price
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(clinic, flags)
        parcel.writeTypedList(servicePrices)
        parcel.writeString(imageURL)
        parcel.writeByte(if (expanded) 1 else 0)
        parcel.writeByte(if (bookmarked) 1 else 0)
        parcel.writeDouble(totalPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClinicListItem> {
        override fun createFromParcel(parcel: Parcel): ClinicListItem {
            return ClinicListItem(parcel)
        }

        override fun newArray(size: Int): Array<ClinicListItem?> {
            return arrayOfNulls(size)
        }
    }
}
