package com.dmytroandriichuk.cmediacal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.dmytroandriichuk.cmediacal.db.DatabaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetailsViewModel(private val filters: List<String>, private val localDBRepository: DatabaseRepository) : ViewModel() {
    private val firebaseFirestoreDB = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val _searchDoc = MutableLiveData<ClinicListItem>()
    val searchDoc: LiveData<ClinicListItem> = _searchDoc

    private val firestoreImagesRef = FirebaseFirestore.getInstance().collection("Images").whereEqualTo("validated", true)

    private val _imagesRef = MutableLiveData<List<String>>()
    val imagesRef: LiveData<List<String>> = _imagesRef

    fun insert(clinic: Clinic) {
        localDBRepository.insert(clinic)
    }

    fun delete(clinic: Clinic) {
        localDBRepository.delete(clinic)
    }

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

    fun getImages(tag: String, id: String) {
        firestoreImagesRef
                .whereEqualTo("clinic_id", id)
                .whereEqualTo(tag, true)
                .get()
                .addOnSuccessListener { docs ->
                    _imagesRef.value = docs.map { it.id }
                }
    }

    class DetailsViewModelFactory(private val filters: List<String>, private val localDBRepository: DatabaseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailsViewModel(filters, localDBRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}