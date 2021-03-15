package com.dmytroandriichuk.cmediacal.fragments.review

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.LandingActivity
import com.dmytroandriichuk.cmediacal.R
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException


class LeaveReviewFragment : Fragment(), ImagesAdapter.DeleteItemListener {

    //constant to track image chooser intent
    private val PICK_IMAGE_REQUEST = 100
    private val leaveReviewViewModel: LeaveReviewViewModel by activityViewModels()
    private lateinit var contentResolver: ContentResolver
    private lateinit var loadingRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_leave_review, container, false)

        val mToolbar = root.findViewById<Toolbar>(R.id.toolbar2)
        (activity as LandingActivity).setSupportActionBar(mToolbar)
        mToolbar.setNavigationIcon(R.drawable.ic_log_out)
        mToolbar.setNavigationOnClickListener {
            FirebaseAuth.getInstance().signOut()
            (activity as LandingActivity).finish()
        }
        loadingRecyclerView = root.findViewById(R.id.loadingRecyclerView)
        loadingRecyclerView.layoutManager = LinearLayoutManager(activity)
        loadingRecyclerView.adapter = ImagesAdapter(leaveReviewViewModel.loadingItems, this)
        contentResolver = (activity as LandingActivity).contentResolver

        root.findViewById<Button>(R.id.button2).setOnClickListener {
            showFileChooser()
        }
        root.findViewById<Button>(R.id.button).setOnClickListener {
            leaveReviewViewModel.putFiles()
        }

        leaveReviewViewModel.progresses.observe(viewLifecycleOwner, {
            for (i in it.indices) {
                leaveReviewViewModel.loadingItems[i].progress = it[i]
                (loadingRecyclerView.adapter as ImagesAdapter).notifyDataSetChanged()
                Log.d("LeaveReviewFragment", "onCreateView: ${it[i]}")
            }
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data!!

            try {
                val source = ImageDecoder.createSource(contentResolver, filePath)
                val bitmap = ImageDecoder.decodeBitmap(source)
                val newItem = ImagesAdapter.LoadingItem(bitmap, filePath)
                addItem(newItem)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    override fun removeItem(position: Int) {
        leaveReviewViewModel.loadingItems.removeAt(position)
        (loadingRecyclerView.adapter as ImagesAdapter).removeItem(position)
    }

    fun addItem(newItem: ImagesAdapter.LoadingItem) {
        leaveReviewViewModel.loadingItems.add(newItem)
        (loadingRecyclerView.adapter as ImagesAdapter).addItem(newItem)
    }


}