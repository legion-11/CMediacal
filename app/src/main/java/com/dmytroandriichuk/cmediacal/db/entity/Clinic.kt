package com.dmytroandriichuk.cmediacal.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity
data class Clinic (val id: String,
                   val userEmail: String,
                   val name: String,
                   val address : String,
                   val lat: Double = 0.0,
                   val lng: Double = 0.0,
                   val phone: String = "",
                   val date : Long = Date().time,
                   @PrimaryKey val crossRefId: String = id + userEmail,
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString()!!,
            parcel.readLong(),
            parcel.readString()!!) {
    }

    override fun toString(): String {
        return "Clinic(id='$id', userEmail='$userEmail', name='$name', address='$address', lat=$lat, lng=$lng, date=$date, crossRefId='$crossRefId')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userEmail)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(phone)
        parcel.writeLong(date)
        parcel.writeString(crossRefId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Clinic> {
        override fun createFromParcel(parcel: Parcel): Clinic {
            return Clinic(parcel)
        }

        override fun newArray(size: Int): Array<Clinic?> {
            return arrayOfNulls(size)
        }
    }
}

