package com.example.camera2api

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.camera2api.databinding.ItemListBinding
import java.io.File

class CameraImagesAdapter(var context: Context, private var imagesList: List<File>):
    RecyclerView.Adapter<CameraImagesAdapter.MyViewHolder>() {

    class MyViewHolder(private var binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imagesFile: File) {
            // Load and display the photo using Glide
            Glide.with(itemView).load(imagesFile).centerCrop().into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding = ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      holder.bind(imagesList[position])
    }
}