package com.dmytroandriichuk.cmediacal.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.dmytroandriichuk.cmediacal.DetailsActivity
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.adapter.ViewPagerParentAdapter

class ImagesDialog(private val imagesId: List<String>): AppCompatDialogFragment(){

    private lateinit var viewPager2: ViewPager2

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.slider, null)
            builder.setView(view)

            //each element of recycleView is title with chip group
            viewPager2 = view.findViewById(R.id.viewPager) as ViewPager2
            viewPager2.adapter = ViewPagerParentAdapter(imagesId, activity as Context)
            Log.d("ImagesDialog", "onCreateDialog: ${imagesId}")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}