package com.dmytroandriichuk.cmediacal.db.entity

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
){
    override fun toString(): String {
        return "ServicePrice(id=$id, serviceName='$serviceName', price=$price, crossRefServiceId='$crossRefServiceId')"
    }
}
