package com.dmytroandriichuk.cmediacal.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.dmytroandriichuk.cmediacal.GlideApp
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.dmytroandriichuk.cmediacal.data.ValidationData
import com.dmytroandriichuk.cmediacal.db.DatabaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.random.Random

class DetailsViewModel(private val filters: List<String>, private val localDBRepository: DatabaseRepository) : ViewModel() {
    private val firebaseFirestoreDB = FirebaseFirestore.getInstance()
    private var imageRef = Firebase.storage.reference.child("Images")
    private val mAuth = FirebaseAuth.getInstance()
    private val _searchDoc = MutableLiveData<ClinicListItem>()
    val searchDoc: LiveData<ClinicListItem> = _searchDoc
    private val _searchValidationResult: MutableLiveData<ValidationData> = MutableLiveData()
    val searchValidationResult: LiveData<ValidationData> = _searchValidationResult

    private val random = Random(Date().time)
    private val firestoreImagesRef = firebaseFirestoreDB.collection("Images")

    private val _imagesId = MutableLiveData<List<String>>()
    val imagesId: LiveData<List<String>> = _imagesId

    fun insert(clinic: Clinic) {
        localDBRepository.insert(clinic)
    }

    fun delete(clinic: Clinic) {
        localDBRepository.delete(clinic)
    }

    // get clinic by id
    fun getClinicData(id: String){
        firebaseFirestoreDB.collection("Dental Clinics").document(id).get()
                .addOnSuccessListener { doc ->
                    val clinic = parseClinic(doc)
                    val services = parseAllServices(doc)
                    _searchDoc.value = ClinicListItem(clinic, services)
                }
    }

    // get all images id that have information about service we need
    fun getImages(tag: String, id: String) {
        firestoreImagesRef
                .whereEqualTo("validated", true)
                .whereEqualTo("clinic_id", id)
                .whereEqualTo(tag, true)
                .get()
                .addOnSuccessListener { docs ->
                    _imagesId.value = docs.map { it.id }
                }
    }

    fun searchForValidation(context: Context) {
        //load images data
        firestoreImagesRef
            .whereEqualTo("validated", false)
            .limit(20)
            .get()
            .addOnSuccessListener { docsImages ->
                val size = docsImages.documents.size
                // if no images for validation
                if (size == 0) { return@addOnSuccessListener }
                val chosenDoc = docsImages.documents[random.nextInt(size)]
                val services = parseAllServices(chosenDoc)
                //load clinic data
                firebaseFirestoreDB.collection("Dental Clinics")
                        .document(chosenDoc["clinic_id"] as String)
                        .get()
                        .addOnSuccessListener { docClinic ->
                            val clinic = parseClinic(docClinic)
                            //load images itself
                            Log.d("TAG", "searchForValidation: ${chosenDoc.id}")
                            imageRef.child(chosenDoc.id).listAll().addOnSuccessListener{ imageSnapshots ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val listOfImages = Array(imageSnapshots.items.size) { i ->
                                        GlideApp.with(context)
                                                .asBitmap()
                                                .load(imageSnapshots.items[i])
                                                .submit()
                                                .get()
                                    }
                                    withContext(Dispatchers.Main) {
                                        _searchValidationResult.value = ValidationData(chosenDoc.id, ClinicListItem(clinic, services), listOfImages)
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Log.d("DetailsViewModel", "searchForValidation: ${it.message}")
                            }
                        }
                        .addOnFailureListener {
                            Log.d("DetailsViewModel", "searchForValidation: ${it.message}")
                        }
            }
            .addOnFailureListener {
                Log.d("DetailsViewModel", "searchForValidation: ${it.message}")
            }
    }

    //parsing all services from dataSnapshot and create list of ServicePrice instances
    private fun parseAllServices(doc: DocumentSnapshot): List<ServicePrice>{
        return filters.mapNotNull { serviceName ->
            val userEmail = mAuth.currentUser?.email ?: "default"
            val price = doc[serviceName].toString().toDoubleOrNull()
            price?.let {
                ServicePrice(0, serviceName, price , doc.id  + userEmail)
            }
        }
    }

    //parsing  dataSnapshot and create Clinic instance
    private fun parseClinic(doc: DocumentSnapshot): Clinic{
        return Clinic(
                doc.id,
                mAuth.currentUser?.email ?: "default",
                doc["name"] as String? ?: "placeholder",
                doc["address"] as String? ?: "placeholder",
                doc["lat"] as Double? ?: 0.0,
                doc["lng"] as Double? ?: 0.0,
        )
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