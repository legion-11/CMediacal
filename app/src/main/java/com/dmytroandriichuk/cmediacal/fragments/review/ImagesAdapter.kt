package com.dmytroandriichuk.cmediacal.fragments.review

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.data.ClinicListItem
import com.google.android.gms.maps.*
import java.util.*
import kotlin.collections.ArrayList

//first adapter for the recycleView with nested items
class ImagesAdapter(private var dataSet: MutableList<LoadingItem>, private val deleteItemListener: DeleteItemListener):
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    val TAG = "ImagesAdapter"

    fun addItem(item: LoadingItem){
        notifyItemInserted(dataSet.size-1)
        Log.d(TAG, "addItem: $dataSet")
    }

    fun removeItem(position: Int){
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataSet.size);
        Log.d(TAG, "removeItem: $dataSet")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.load_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.deleteBtn.setOnClickListener {
            deleteItemListener.removeItem(position)
        }
        holder.image.setImageBitmap(item.bitmap)
        holder.progress.progress = item.progress
    }

    //number of items in recycleView
    override fun getItemCount(): Int = dataSet.size

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.loadingImageView)
        val progress: ProgressBar = view.findViewById(R.id.loadingProgressBar)
        val deleteBtn: ImageButton = view.findViewById(R.id.loadingDeleteBtn)
    }

    interface DeleteItemListener {
        fun removeItem(position: Int)
    }

    class LoadingItem(val bitmap: Bitmap, val uri: Uri, var progress: Int = 0)
}