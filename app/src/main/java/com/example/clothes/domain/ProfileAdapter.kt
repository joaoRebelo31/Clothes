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
import com.example.clothes.model.Post
import com.example.clothes.model.Profile

class ProfileAdapter(
    private val profile: Profile,
    private val postsList: List<Post>,
    private val context: Context,
    private val itemClickedListener: OnItemClickListener?
) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    interface OnItemClickListener { fun invoke(position: Int, profile: Profile) }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postItemBinding = ItemGridImageBinding.bind(itemView)

        fun bindView(post: Post, position: Int, itemClickedListener: OnItemClickListener?) {
            itemView.setOnClickListener{
                itemClickedListener?.invoke(position, profile)
            }
            Glide.with(context).load(post.image).into(postItemBinding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_grid_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postsList[position]
        holder.bindView(post, position, itemClickedListener)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }
}