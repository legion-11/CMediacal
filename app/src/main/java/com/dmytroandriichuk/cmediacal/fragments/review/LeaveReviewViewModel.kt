package com.dmytroandriichuk.cmediacal.fragments.review

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class LeaveReviewViewModel : ViewModel() {

    private var imageRef = Firebase.storage.reference.child("Images")
    private val firebaseFirestoreDB = FirebaseFirestore.getInstance().collection("Images")

    val loadingItems: MutableList<ImagesAdapter.LoadingItem> = ArrayList()
    val formItems: MutableList<FormAdapter.FormItem> = ArrayList()

    val progresses: MutableLiveData<Array<Int>> = MutableLiveData()
    var isUploading = false

    fun putFiles() {
        if (loadingItems.size == 0) return
        progresses.value = Array(loadingItems.size) { 0 }

        // push new doc to firestore to get unique id
        firebaseFirestoreDB.add(hashMapOf("somedata" to "somedata"))
            .addOnSuccessListener { docRef ->
                val ref = imageRef.child(docRef.id)
                isUploading = true
                var numberOfUploadingFiles = loadingItems.size
                for (i in 0 until loadingItems.size) {
                    // each image will have path images/<docRef.id>/<i>
                    val fileRef = ref.child(i.toString())
                    fileRef.putFile(loadingItems[i].uri)
                        .addOnProgressListener {
                            progresses.value!![i] = (it.bytesTransferred.toFloat() / it.totalByteCount.toFloat() * 100).toInt()
                            progresses.value = progresses.value
                        }
                        .addOnCompleteListener {
                            if (it.isComplete) {
                                numberOfUploadingFiles -= 1
                                if (numberOfUploadingFiles == 0) {
                                    isUploading = false
                                }
                            }
                        }
                }

            }
            .addOnFailureListener {
                Log.d("TAG", "putFiles: error")
            }
    }
}