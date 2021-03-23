package com.dmytroandriichuk.cmediacal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.DetailsActivity
import com.dmytroandriichuk.cmediacal.GlideApp
import com.dmytroandriichuk.cmediacal.R
import com.google.firebase.storage.StorageReference

class ViewPagerChildAdapter(private val imagesId: List<StorageReference>, private val context: Context) : RecyclerView.Adapter<ViewPagerChildAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_page, parent, false))
    }

    override fun getItemCount(): Int = imagesId.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //todo load with gs
        GlideApp.with(context /* context */)
            .load(imagesId[position])
            .into(holder.image)
        Log.d("ViewPagerChildAdapter", "onBindViewHolder: ${imagesId[position]}")
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.dialogImage)
    }
}
