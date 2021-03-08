package com.dmytroandriichuk.cmediacal.ui.search

import androidx.lifecycle.*
import com.dmytroandriichuk.cmediacal.db.DatabaseRepository
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListParentItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

//for future usage of live data from firebase db
class SearchViewModel(private val localDBRepository: DatabaseRepository) : ViewModel() {

    private val firebaseFirestoreDB = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    private val _searchItems = MutableLiveData<ArrayList<SearchListParentItem>>()
    val searchItems: LiveData<ArrayList<SearchListParentItem>> = _searchItems
    private val _bookmarks: LiveData<Array<Clinic>> = localDBRepository.getAllClinics(getUser()).asLiveData()
    private val _observer = Observer {
        array: Array<Clinic> -> bookmarks = array
        val map = array.map { it.id }
        _searchItems.value?.forEach { item ->
            item.bookmarked = item.id in map
        }
    }
    private lateinit var bookmarks: Array<Clinic>

    val provinces: ArrayList<String> = ArrayList()
    val filters: ArrayList<String> = ArrayList()
    var firstCall = true

    init {
        _bookmarks.observeForever (_observer)
    }

    override fun onCleared() {
        _bookmarks.removeObserver(_observer)
        super.onCleared()
    }

    fun getUser(): String {
        return mAuth.currentUser?.email ?: "default"
    }

    fun insert(clinic: Clinic) {
        localDBRepository.insert(clinic)
    }

    fun delete(clinic: Clinic) {
        localDBRepository.delete(clinic)
    }

    fun insert(servicePrice: ServicePrice) {
        localDBRepository.insert(servicePrice)
    }

    fun delete(servicePrice: ServicePrice) {
        localDBRepository.delete(servicePrice)
    }

    fun searchQuery(){
        val ref = firebaseFirestoreDB.collection("Dental Clinics")
        var query: Query? = null
        if (provinces.isNotEmpty()){
            query = ref.whereIn("Province", provinces)
        }
        for (filter in filters){
            query = (query ?: ref).whereEqualTo("tag $filter", true)
        }

        (query ?: ref).get().addOnSuccessListener {  docs ->
            setSearchItems(docs, filters)
        }

    }

    private fun setSearchItems(query: QuerySnapshot, filters: ArrayList<String>){
        val bookmarksID = bookmarks.map { it.id }
        _searchItems.value = query.map { doc ->
            SearchListParentItem(doc.id).apply {
                (doc["name"] as String?)?.let { name = it }
                (doc["address"] as String?)?.let { address = it }
                (doc["imageURL"] as String?)?.let { imageURL = it }
                updateServicesPrices(filters.associateWith {
                    doc[it] as Double
                })

                (doc["lat"] as Double?)?.let { lat = it }
                (doc["lng"] as Double?)?.let { lng = it }
                //TODO bookmark check
                if (doc.id in bookmarksID){
                    bookmarked = true
                }
            }
        } as ArrayList
    }

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

