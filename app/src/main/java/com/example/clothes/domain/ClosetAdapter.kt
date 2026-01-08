package com.example.clothes.domain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothes.model.Cloth
import com.example.clothes.R
import com.example.clothes.databinding.ItemGridImageBinding

class ClosetAdapter(
    private val clothesList: List<Cloth?>,
    private val context: Context,
    private val itemClickedListener: OnItemClickListener?
) : RecyclerView.Adapter<ClosetAdapter.ViewHolder>() {

    interface OnItemClickListener { fun invoke(cloth: Cloth) }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clothItemBinding = ItemGridImageBinding.bind(itemView)

        fun bindView(cloth: Cloth, itemClickedListener: OnItemClickListener?) {
            itemView.setOnClickListener{
                itemClickedListener?.invoke(cloth)
            }
            Glide.with(context).load(cloth.image).into(clothItemBinding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_grid_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cloth = clothesList[position]
        if (cloth != null) { holder.bindView(cloth, itemClickedListener) }
    }

    override fun getItemCount(): Int {
        return clothesList.size
    }
}