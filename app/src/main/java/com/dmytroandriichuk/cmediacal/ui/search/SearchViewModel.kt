package com.dmytroandriichuk.cmediacal.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmytroandriichuk.cmediacal.db.DatabaseRepository
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListParentItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

//for future usage of live data from firebase db
class SearchViewModel(private val localDBRepository: DatabaseRepository) : ViewModel() {

    private val firebaseFirestoreDB = FirebaseFirestore.getInstance()

    private val _clinicItems = MutableLiveData<ArrayList<SearchListParentItem>>()

    fun insert(clinic: Clinic) {
        localDBRepository.insert(clinic)
    }

    fun delete(clinic: Clinic) {
        localDBRepository.delete(clinic)
    }

    fun searchQuery(provinces: ArrayList<String>, filters: ArrayList<String>){
        val ref = firebaseFirestoreDB.collection("Dental Clinics")
        var query: Query? = null
        if (provinces.isNotEmpty()){
            query = ref.whereIn("Province", provinces)
        }
        for (filter in filters){
            query = query?.whereEqualTo("tag $filter", true) ?: ref.whereEqualTo("tag $filter", true)
        }

        //TODO add search if no filters
        query?.let {
            query.get().addOnSuccessListener { docs ->
                setClinicItems(docs, filters)
            }
        }
    }

    private fun setClinicItems(query: QuerySnapshot, filters: ArrayList<String>){
        _clinicItems.value = query.map { doc ->
            SearchListParentItem(doc.id).apply {
                (doc["name"] as String?)?.let { name = it }
                (doc["address"] as String?)?.let { address = it }
                (doc["imageURL"] as String?)?.let { imageURL = it }
                updateServicesPrices(filters.associateWith {
                    doc[it] as Double
                })
                //TODO bookmark check

            }
        } as ArrayList
    }
    val clinicItems: LiveData<ArrayList<SearchListParentItem>> = _clinicItems

    class SearchViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

