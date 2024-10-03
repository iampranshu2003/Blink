package com.example.adminblink.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminblink.databinding.ItemViewImageSelectionBinding

class AdapterSelectedImage(val ImageUris : ArrayList<Uri>): RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder>() {
    class SelectedImageViewHolder(val binding: ItemViewImageSelectionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        return SelectedImageViewHolder(ItemViewImageSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return ImageUris.size
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        holder.binding.apply {
            ivImage.setImageURI(ImageUris[position])
        }
        holder.binding.ibClose.setOnClickListener {
            if (position < ImageUris.size) {
                ImageUris.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, ImageUris.size) // This ensures consistency in the adapter
            }
        }
    }

}