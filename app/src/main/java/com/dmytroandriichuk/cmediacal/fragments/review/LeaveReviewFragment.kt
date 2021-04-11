package com.dmytroandriichuk.cmediacal.fragments.review

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.LandingActivity
import com.dmytroandriichuk.cmediacal.R
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.IOException
import java.util.*


class LeaveReviewFragment : Fragment(), ImagesAdapter.DeleteItemListener, FormAdapter.ChangeDataSetListener {

    //constant to track image chooser intent
    private val leaveReviewViewModel: LeaveReviewViewModel by activityViewModels()
    private lateinit var contentResolver: ContentResolver
    private lateinit var loadingRecyclerView: RecyclerView
    private lateinit var formRecyclerView: RecyclerView
    private lateinit var addressET: EditText
    private lateinit var geocoder: Geocoder
    private lateinit var fileUri: Uri

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_leave_review, container, false)

        geocoder = Geocoder(activity, Locale.CANADA)

        // when you click on addressET it will invoke intent to find place with google autocomplete
        addressET = root.findViewById(R.id.addressET)
        addressET.setText(leaveReviewViewModel.clinicAddress)
        addressET.setOnClickListener {
            val fieldList = listOf(
                    Place.Field.ID,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG,
                    Place.Field.NAME,
                    Place.Field.TYPES,
                    Place.Field.PHONE_NUMBER,
            )

            val autocompleteIntent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    fieldList
            ).setCountry("CA").build(activity as LandingActivity)
            startActivityForResult(autocompleteIntent, PICK_ADDRESS_REQUEST)
        }

        val titles: Array<String> = resources.getStringArray(R.array.filter_titles)
        val filterResources: TypedArray = resources.obtainTypedArray(R.array.filters_resources)

        val formItemsMap: MutableMap<String, List<String>> = mutableMapOf()
        for (i in 1 until titles.size) {
            val arrayId = filterResources.getResourceId(i, 0)
            formItemsMap += titles[i] to resources.getStringArray(arrayId).toList()
        }
        filterResources.recycle()

        //recycler view to input service and it's price
        formRecyclerView = root.findViewById(R.id.formRecyclerView)
        formRecyclerView.layoutManager = LinearLayoutManager(activity)
        formRecyclerView.adapter = FormAdapter(leaveReviewViewModel.formItems, formItemsMap, this)

        //recycler view to input images of bills
        loadingRecyclerView = root.findViewById(R.id.loadingRecyclerView)
        loadingRecyclerView.layoutManager = LinearLayoutManager(activity)
        loadingRecyclerView.adapter = ImagesAdapter(leaveReviewViewModel.loadingItems, this)
        contentResolver = (activity as LandingActivity).contentResolver

        root.findViewById<Button>(R.id.confirmButton).setOnClickListener {
            if (leaveReviewViewModel.clinicId == null) {
                Toast.makeText(activity, "No address selected", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (FirebaseAuth.getInstance().currentUser == null) {
                Toast.makeText(activity, "You are not authenticated", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val imagesData = mutableMapOf<String, Any>()
            val keys = formItemsMap.keys.toList()
            for (i in leaveReviewViewModel.formItems) {
                val category = keys[i.positionCategory]
                val subcategory = formItemsMap[category]?.get(i.positionSubcategory)
                imagesData["$category: $subcategory"] = i.price
                imagesData["tag $category: $subcategory"] = true
            }

            imagesData["clinic_id"] = leaveReviewViewModel.clinicId!!
            imagesData["validated"] = false
            imagesData["date"] = Date().time
            imagesData["userId"] = FirebaseAuth.getInstance().currentUser!!.uid
            leaveReviewViewModel.putFiles(imagesData)
        }

        leaveReviewViewModel.progresses.observe(viewLifecycleOwner, {
            for (i in it.indices) {
                leaveReviewViewModel.loadingItems[i].progress = it[i]
                (loadingRecyclerView.adapter as ImagesAdapter).notifyDataSetChanged()
                Log.d("LeaveReviewFragment", " progress position $i: ${it[i]}")
            }
        })

        leaveReviewViewModel.message.observe(viewLifecycleOwner, { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        })

        return root
    }

    private fun showFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // request for getting photo from galery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data!!
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, filePath)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                }

                val newItem = ImagesAdapter.LoadingItem(bitmap, filePath)
                addItem(newItem)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        // request for google autocomplete
        } else if (requestCode == PICK_ADDRESS_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                addressET.setText(place?.address)
                leaveReviewViewModel.clinicId = place?.id
                leaveReviewViewModel.clinicAddress = place?.address

                leaveReviewViewModel.clinicData["address"] = place?.address.toString()
                leaveReviewViewModel.clinicData["name"] = place?.name.toString()
                leaveReviewViewModel.clinicData["phone"] = place?.phoneNumber.toString()

                if (place?.latLng != null) {
                    val region = geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1)[0].adminArea
                    leaveReviewViewModel.clinicData["Province"] = STATES[region.toString()] ?: region
                    leaveReviewViewModel.clinicData["lat"] = place.latLng!!.latitude
                    leaveReviewViewModel.clinicData["lng"] = place.latLng!!.longitude
                    Log.d("TAG", "onActivityResult: $region")
                    Log.d("TAG", "onActivityResult: ${place.types}")
                }

            }  else {
                val message = data?.let { Autocomplete.getStatusFromIntent(it).statusMessage }
                message?.let {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                }
                Log.d("TAG", "onActivityResult: $message")
            }
        // request to camera app
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, fileUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }

            val newItem = ImagesAdapter.LoadingItem(bitmap, fileUri)
            addItem(newItem)
        }
    }


    override fun removeImageItem(position: Int) {
        if (leaveReviewViewModel.isUploading) return
        leaveReviewViewModel.loadingItems.removeAt(position)
        (loadingRecyclerView.adapter as ImagesAdapter).removeItem(position)
    }

    override fun addImageItem() {
        showFileChooser()
    }

    override fun addFromCameraItem() {
        context?.let {
            if (checkSelfPermission(it, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val file = File.createTempFile(System.currentTimeMillis().toString(), ".jpg", storageDir)
                context?.let {
                    fileUri = FileProvider.getUriForFile(it, "com.dmytroandriichuk.cmediacal.fileprovider", file)
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addItem(newItem: ImagesAdapter.LoadingItem) {
        leaveReviewViewModel.loadingItems.add(newItem)
        (loadingRecyclerView.adapter as ImagesAdapter).addItem()
        Log.d("TAG", "addItem: ${leaveReviewViewModel.loadingItems.size}")
    }

    override fun removeFormItem(position: Int) {
        if (leaveReviewViewModel.isUploading) return
        leaveReviewViewModel.formItems.removeAt(position)
        (formRecyclerView.adapter as FormAdapter).onItemRemoved(position)
    }

    override fun addFormItem() {
        leaveReviewViewModel.formItems.add(FormAdapter.FormItem())
        (formRecyclerView.adapter as FormAdapter).onItemAdded()
    }

    companion object {
        val STATES = hashMapOf(
                "Alberta" to "AB",
                "British Columbia" to "BC",
                "Manitoba" to "MB",
                "New Brunswick" to "NB",
                "Newfoundland and Labrador" to "NL",
                "Northwest Territories" to "NT",
                "Nova Scotia" to "NS",
                "Nunavut" to "NU",
                "Ontario" to "ON",
                "Prince Edward Island" to "PE",
                "Quebec" to "QC",
                "Québec" to "QC",
                "Saskatchewan" to "SK",
                "Yukon Territory" to "YT",
        )
        const val PICK_IMAGE_REQUEST = 100
        const val PICK_ADDRESS_REQUEST = 200
        const val CAMERA_REQUEST = 300
        const val CAMERA_PERMISSION_CODE = 301;
    }
}