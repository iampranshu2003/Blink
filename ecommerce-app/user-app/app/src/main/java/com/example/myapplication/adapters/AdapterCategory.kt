package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemViewProductCategoryBinding
import com.example.myapplication.models.Category

class AdapterCategory(
    val categoryList: ArrayList<Category>,
    val onCategoryIconClicked: (Category) -> Unit
) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    class CategoryViewHolder ( val binding: ItemViewProductCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.apply {
            ivCategoryImage.setImageResource(categoryList[position].image!!)
            tvCategoryName.text = categoryList[position].title

        }
        holder.itemView.setOnClickListener {
            onCategoryIconClicked(categoryList[position])
        }

    }


}