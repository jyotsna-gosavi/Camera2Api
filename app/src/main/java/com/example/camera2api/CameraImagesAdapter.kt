package com.example.camera2api

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.camera2api.databinding.ItemListBinding

class CameraImagesAdapter(
    var context: Context,
    private var imagesList: List<ImageModel>,
    private var itemClickListener: OnImageItemClickListener
) :
    RecyclerView.Adapter<CameraImagesAdapter.MyViewHolder>() {
    interface OnImageItemClickListener {
        fun onItemClick(imagePath: String)
    }

    class MyViewHolder(
        private var binding: ItemListBinding,
        private val imagesList: List<ImageModel>,
        private val itemClickListener: OnImageItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(imageModel: ImageModel) {
            // Load and display the photo using Glide
            Glide.with(itemView.context)
                .load(imageModel.imagePath)
                .centerCrop()
                .into(binding.imageView)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val imagePath = imagesList[position].imagePath
                itemClickListener.onItemClick(imagePath)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, imagesList, itemClickListener)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(imagesList[position])

    }
    // Other adapter methods

    fun updateData(newImagesList: List<ImageModel>) {
        imagesList = newImagesList // Update the dataset
        notifyDataSetChanged() // Notify adapter that dataset has changed
    }

}