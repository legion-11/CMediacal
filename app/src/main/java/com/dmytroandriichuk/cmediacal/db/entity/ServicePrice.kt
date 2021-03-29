package com.dmytroandriichuk.cmediacal.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Clinic::class,
        parentColumns = arrayOf("crossRefId"),
        childColumns = arrayOf("crossRefServiceId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ServicePrice(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val serviceName: String,
    val price: Double,
    @ColumnInfo(index = true)
    val crossRefServiceId: String
): Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()!!,
            parcel.readDouble(),
            parcel.readString()!!) {
    }

    override fun toString(): String {
        return "ServicePrice(id=$id, serviceName='$serviceName', price=$price, crossRefServiceId='$crossRefServiceId')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(serviceName)
        parcel.writeDouble(price)
        parcel.writeString(crossRefServiceId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServicePrice> {
        override fun createFromParcel(parcel: Parcel): ServicePrice {
            return ServicePrice(parcel)
        }

        override fun newArray(size: Int): Array<ServicePrice?> {
            return arrayOfNulls(size)
        }
    }
}
