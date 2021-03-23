package com.dmytroandriichuk.cmediacal.db.entity

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
) {
    override fun toString(): String {
        return "Clinic(id='$id', userEmail='$userEmail', name='$name', address='$address', lat=$lat, lng=$lng, date=$date, crossRefId='$crossRefId')"
    }
}

