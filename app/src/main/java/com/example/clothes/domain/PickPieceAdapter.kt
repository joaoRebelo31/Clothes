package com.example.clothes.domain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothes.R
import com.example.clothes.model.Cloth

class PickPieceAdapter(
    private val context: Context,
    private val clothList: List<Cloth>, // List of drawable resource IDs
    private val onImageClickListener: (Cloth) -> Unit
) : RecyclerView.Adapter<PickPieceAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_grid_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val cloth = clothList[position]
        Glide.with(context).load(cloth.image).into(holder.imageView)
        holder.imageView.setOnClickListener {
            onImageClickListener(cloth)
        }
    }

    override fun getItemCount(): Int {
        return clothList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
    }
}