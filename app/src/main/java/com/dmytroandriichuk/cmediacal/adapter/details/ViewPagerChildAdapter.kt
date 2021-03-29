package com.dmytroandriichuk.cmediacal.adapter.details

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.GlideApp
import com.dmytroandriichuk.cmediacal.R
import com.google.firebase.storage.StorageReference

class ViewPagerChildAdapter(private val imagesRef: List<StorageReference>, private val context: Context) : RecyclerView.Adapter<ViewPagerChildAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_page, parent, false))
    }

    override fun getItemCount(): Int = imagesRef.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        GlideApp.with(context)
            .load(imagesRef[position])
            .into(holder.image)
        Log.d("ViewPagerChildAdapter", "onBindViewHolder: ${imagesRef[position]}")
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.zoomableImage)
    }
}
