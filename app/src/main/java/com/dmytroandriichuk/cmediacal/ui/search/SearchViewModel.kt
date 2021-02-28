package com.dmytroandriichuk.cmediacal.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dmytroandriichuk.cmediacal.ui.search.model.SearchListParentItem
import com.google.firebase.firestore.QuerySnapshot

//for future usage of live data from firebase db
class SearchViewModel : ViewModel() {

    private val _clinicItems = MutableLiveData<ArrayList<SearchListParentItem>>()
    fun setClinicItems(query: QuerySnapshot, filters: ArrayList<String>){
        _clinicItems.value = query.map { doc ->
            SearchListParentItem().apply {
                (doc["name"] as String?)?.let { name = it }
                (doc["address"] as String?)?.let { address = it }
                (doc["imageURL"] as String?)?.let { imageURL = it }
                updateServicesPrices(filters.associateWith {
                    doc[it] as Double
                })
            }
        } as ArrayList
    }
    val clinicItems: LiveData<ArrayList<SearchListParentItem>> = _clinicItems
}