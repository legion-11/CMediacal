package com.dmytroandriichuk.cmediacal.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import java.io.ByteArrayOutputStream
import java.io.IOException

data class ValidationData(
    val id: String,
    val clinicListItem: ClinicListItem,
    val images: Array<Bitmap>
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(ClinicListItem::class.java.classLoader)!!,
        getBitmapArray(parcel)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(clinicListItem, flags)
        val size = images.size ?: 0
        parcel.writeInt(size)
        for (i in 0 until size) {
            val byteArray = convert(images[i])
            parcel.writeInt(byteArray.size)
            parcel.writeByteArray(byteArray)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

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


    companion object CREATOR : Parcelable.Creator<ValidationData> {
        override fun createFromParcel(parcel: Parcel): ValidationData {
            return ValidationData(parcel)
        }

        override fun newArray(size: Int): Array<ValidationData?> {
            return arrayOfNulls(size)
        }

        @Throws(IOException::class)
        fun convert(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val array: ByteArray = stream.toByteArray()
            stream.close()
            return array
        }

        private fun convert(array: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(array, 0, array.size)
        }
        fun getBitmapArray(parcel: Parcel): Array<Bitmap>{
            val sizeOfArray = parcel.readInt()
            return Array(sizeOfArray) {
                val imageByteNumber = parcel.readInt()
                val byteArray = ByteArray(imageByteNumber)
                parcel.readByteArray(byteArray)
                convert(byteArray)
            }
        }
    }

}