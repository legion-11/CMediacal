package com.dmytroandriichuk.cmediacal.fragments.bookmarks

import androidx.lifecycle.*
import com.dmytroandriichuk.cmediacal.db.DatabaseRepository
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ClinicAndServicePrices
import com.google.firebase.auth.FirebaseAuth

class BookmarksViewModel(private val localDBRepository: DatabaseRepository) : ViewModel() {
    private val mAuth = FirebaseAuth.getInstance()
    val bookmarks: LiveData<Array<ClinicAndServicePrices>> = localDBRepository.getAllClinicsWithPrices(getUser()).asLiveData()

    private fun getUser(): String {
        return mAuth.currentUser?.email ?: "default"
    }

    fun insert(clinic: Clinic) {
        localDBRepository.insert(clinic)
    }

    fun delete(clinic: Clinic) {
        localDBRepository.delete(clinic)
    }

    class BookmarksViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookmarksViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BookmarksViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}