package com.dmytroandriichuk.cmediacal.adapter.validate

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R
import com.dmytroandriichuk.cmediacal.view.ZoomClass

class ValidateViewPagerAdapter(private val images: Array<Bitmap>,
                               private val onImageClickListener: OnImageClickListener):
    RecyclerView.Adapter<ValidateViewPagerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_page, parent, false))
    }

    override fun getItemCount(): Int = images.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("ValidatePagerAdapter", "onBindViewHolder: $position")
        var tapBreak = false
        holder.image.setImageBitmap(images[position])
        holder.image.setOnTouchListener { v, event ->
            (v as ZoomClass).onTouch(v, event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tapBreak = false
                }
                MotionEvent.ACTION_UP -> {
                    if (!tapBreak) {
                        if (v.mode != ZoomClass.NONE) {
                            onImageClickListener.onImageClick()
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    tapBreak = true
                }
                else -> {}
            }


            false
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ZoomClass = view.findViewById(R.id.zoomableImage)
    }

    interface OnImageClickListener {
        fun onImageClick()
    }
}