package com.dmytroandriichuk.cmediacal.fragments.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class LeaveReviewViewModel : ViewModel() {

    private var imageRef = Firebase.storage.reference.child("Images")
    private var clinicRef = FirebaseFirestore.getInstance().collection("Dental Clinics")
    private val firestoreImagesRef = FirebaseFirestore.getInstance().collection("Images")

    val loadingItems: MutableList<ImagesAdapter.LoadingItem> = ArrayList()
    val formItems: MutableList<FormAdapter.FormItem> = arrayListOf(FormAdapter.FormItem())

    val progresses: MutableLiveData<Array<Int>> = MutableLiveData()
    var isUploading = false
    var clinicId: String? = null
    var clinicAddress: String? = null
    val clinicData = mutableMapOf<String, Any>()
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    fun putFiles(imagesData: Map<String, Any>) {
        if (loadingItems.size == 0 || imagesData.isEmpty()) {
            setMessage("No images or details provided")
            return
        }
        isUploading = true
        progresses.value = Array(loadingItems.size) { 0 }

        // create clinic document if it does not exist
        clinicId?.let {
            clinicRef.document(it).set(clinicData, SetOptions.merge())
                .addOnSuccessListener {
                    // create firestore doc for storing the user inputted data
                    firestoreImagesRef.add(imagesData)
                            .addOnSuccessListener { docRef ->
                                loadAllImagesOnFirebaseStorage(docRef)
                            }
                            .addOnFailureListener { setMessage(it.message) }
                }.addOnFailureListener { setMessage(it.message) }
        } ?: run {
            setMessage("This clinic is impossible to add")
        }
    }

    /* loading all selected images to firebase storage */
    private fun loadAllImagesOnFirebaseStorage(docRef: DocumentReference) {
        val ref = imageRef.child(docRef.id)
        var numberOfUploadingFiles = loadingItems.size
        for (i in 0 until loadingItems.size) {
            // each image will have path images/<docRef.id>/<i>
            val fileRef = ref.child(i.toString())
            fileRef.putFile(loadingItems[i].uri)
                    .addOnProgressListener {
                        progresses.value!![i] = (it.bytesTransferred.toFloat() / it.totalByteCount.toFloat() * 100).toInt()
                        progresses.value = progresses.value
                    }
                    .addOnSuccessListener {
                        numberOfUploadingFiles -= 1
                        if (numberOfUploadingFiles == 0) {
                            progresses.value = emptyArray()
                            setMessage("Loading completed")
                        }
                    }
                    .addOnFailureListener { setMessage(it.message) }
        }
    }

    private fun setMessage(m: String?) {
        isUploading = false
        _message.value = m ?: "Something went wrong"
    }

}