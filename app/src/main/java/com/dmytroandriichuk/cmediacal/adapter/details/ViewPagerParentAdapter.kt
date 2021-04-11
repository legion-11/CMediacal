package com.dmytroandriichuk.cmediacal.adapter.details

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dmytroandriichuk.cmediacal.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


// adapter that contains viewPager of viewPagers
// since one image id can contain many images
// child viewPager contains all images of the same image id
class ViewPagerParentAdapter(private val imagesId: List<String>, private val context: Context) : RecyclerView.Adapter<ViewPagerParentAdapter.ViewHolder>() {

    private var imageRef = Firebase.storage.reference.child("Images")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.nested_view_pager, parent, false))

    override fun getItemCount(): Int = imagesId.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        imageRef.child(imagesId[position]).listAll().addOnSuccessListener {
            holder.viewPager.adapter = ViewPagerChildAdapter(it.items, context)
            Log.d("ViewPagerParentAdapter", "onBindViewHolder: ${it.items.size} ${it.items}")
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
    }
}

