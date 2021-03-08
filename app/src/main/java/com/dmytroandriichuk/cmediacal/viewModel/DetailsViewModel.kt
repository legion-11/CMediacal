package com.dmytroandriichuk.cmediacal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailsViewModel(private val filters: List<String>) : ViewModel() {
    private val firebaseFirestoreDB = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val _searchDoc = MutableLiveData<ClinicListItem>()
    val searchDoc: LiveData<ClinicListItem> = _searchDoc

    fun getClinicData(id: String){
        firebaseFirestoreDB.collection("Dental Clinics").document(id).get()
                .addOnSuccessListener { doc ->
                    val clinic = Clinic(
                            doc.id,
                            mAuth.currentUser?.email ?: "default",
                            doc["name"] as String? ?: "placeholder",
                            doc["address"] as String? ?: "placeholder",
                            doc["lat"] as Double? ?: 0.0,
                            doc["lng"] as Double? ?: 0.0,
                    )
                    val services = filters.mapNotNull { serviceName ->
                        val price = doc[serviceName] as Double?
                        price?.let {
                            ServicePrice(0, serviceName, price , clinic.crossRefId)
                        }
                    }
                    _searchDoc.value = ClinicListItem(clinic, services)
                }
    }
    class DetailsViewModelFactory(private val filters: List<String>) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailsViewModel(filters) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}