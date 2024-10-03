package com.example.adminblink.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminblink.databinding.ItemViewProductCategoriesBinding
import com.example.adminblink.model.Categories

class CategoriesAdapter(
    private val categoryList: ArrayList<Categories>,
    val onCategoyClicked: (Categories) -> Unit,
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

        class CategoriesViewHolder(val binding: ItemViewProductCategoriesBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
            return CategoriesViewHolder(ItemViewProductCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int {
            return categoryList.size
        }

        override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
            val category = categoryList[position]


            holder.binding.apply {
                tvCategoryName.text = category.category
                ivCategoryImage.setImageResource(category.icon)
            }
            holder.itemView.setOnClickListener{
                onCategoyClicked(category)

            }
        }

}


