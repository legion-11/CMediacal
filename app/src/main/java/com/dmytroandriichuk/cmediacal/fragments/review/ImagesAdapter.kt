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

//first adapter for the recycleView with nested items
class ImagesAdapter(private var dataSet: MutableList<LoadingItem>, private val deleteItemListener: DeleteItemListener):
    RecyclerView.Adapter<ImagesAdapter.BaseViewHolder>() {

    fun addItem() {
        notifyItemInserted(dataSet.size-1)
    }

    fun removeItem(position: Int){
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataSet.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == 0 ) {
            val view = LayoutInflater.from(parent.context).inflate( R.layout.load_image_item, parent, false)
            ViewHolderWithImage(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.add_image, parent, false)
            ViewHolderAdd(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == dataSet.size) {
            return 1
        }
        return 0
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
            holder as ViewHolderWithImage
            val item = dataSet[position]
            holder.deleteBtn.setOnClickListener {
                deleteItemListener.removeItem(position)
            }
            holder.image.setImageBitmap(item.bitmap)
            holder.progress.progress = item.progress
        } else {
            (holder as ViewHolderAdd).addBtn.setOnClickListener {
                deleteItemListener.addItem()
            }
        }
    }

    //number of items in recycleView
    override fun getItemCount(): Int = dataSet.size + 1

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ViewHolderWithImage(val view: View) : BaseViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.loadingImageView)
        val progress: ProgressBar = view.findViewById(R.id.loadingProgressBar)
        val deleteBtn: ImageButton = view.findViewById(R.id.loadingDeleteBtn)
    }

    class ViewHolderAdd(val view: View) : BaseViewHolder(view) {
        val addBtn: ImageButton = view.findViewById(R.id.addBtn)
    }

    interface DeleteItemListener {
        fun removeItem(position: Int)
        fun addItem()
    }

    class LoadingItem(val bitmap: Bitmap, val uri: Uri, var progress: Int = 0)
}