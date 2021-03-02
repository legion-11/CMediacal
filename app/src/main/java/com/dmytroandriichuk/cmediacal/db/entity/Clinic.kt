package com.dmytroandriichuk.cmediacal.db.entity

import androidx.room.Entity

@Entity(primaryKeys = ["id", "userEmail"])
data class Clinic ( val id: String,
                   val userEmail: String,
                   val name: String,
                   val address : String,
) {
    override fun toString(): String {
        return "Clinic(id='$id', name='$name', address='$address')"
    }
}

