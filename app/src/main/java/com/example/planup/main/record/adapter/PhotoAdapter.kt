package com.example.planup.main.record.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.example.planup.R

class PhotoAdapter(
    imageUrls: List<String>
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private val imageUrls = mutableListOf<String>()
    inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.imageView.load(imageUrls[position]) {
            crossfade(true)
        }
    }

    override fun getItemCount(): Int = imageUrls.size

    fun submitList(newList: List<String>) {
        imageUrls.clear()
        imageUrls.addAll(newList)
        notifyDataSetChanged()
    }
}