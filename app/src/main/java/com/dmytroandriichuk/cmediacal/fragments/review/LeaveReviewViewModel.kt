package com.dmytroandriichuk.cmediacal.fragments.review

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.type.DateTime
import java.time.Instant.now
import java.util.*
import kotlin.collections.ArrayList

class LeaveReviewViewModel : ViewModel() {

    private var imageRef = Firebase.storage.reference.child("Images")
    private val firebaseFirestoreDB = FirebaseFirestore.getInstance().collection("Images")

    val loadingItems: MutableList<ImagesAdapter.LoadingItem> = ArrayList()
    val progresses: MutableLiveData<Array<Int>> = MutableLiveData()
    var isUploading = false

    fun putFiles() {
        isUploading = true
        progresses.value = Array(loadingItems.size) { 0 }
        // push new doc to firestore to get unique id
        firebaseFirestoreDB.add(hashMapOf("somedata" to "somedata"))
            .addOnSuccessListener { docRef ->
                val ref = imageRef.child(docRef.id)
                for (i in 0 until loadingItems.size) {
                    // each image will have path images/<docRef.id>/<i>
                    val fileRef = ref.child(i.toString())
                    fileRef.putFile(loadingItems[i].uri)
                        .addOnProgressListener {
                            progresses.value!![i] = (it.bytesTransferred.toFloat() / it.totalByteCount.toFloat() * 100).toInt()
                            progresses.value = progresses.value
//                            if (progresses.value?.all { it == 100 } == true) {
//                                isUploading = false
//                            }
                        }
                }

            }
            .addOnFailureListener {
                Log.d("TAG", "putFiles: error")
//                isUploading = false
            }
    }
}