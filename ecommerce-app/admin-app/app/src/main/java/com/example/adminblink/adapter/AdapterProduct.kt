package com.example.adminblink.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminblink.FilteringProducts
import com.example.adminblink.databinding.ItemViewProductBinding
import com.example.adminblink.model.Product

class AdapterProduct(
    val onEditClicked: (Product) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable {

    class ProductViewHolder(val binding: ItemViewProductBinding) : ViewHolder(binding.root)

    private val diffutil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomID == newItem.productRandomID
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffutil)

    var originalList = ArrayList<Product>()

    // Use custom getter for filter
    var filter: FilteringProducts? = null
        get() {
            if (field == null) {
                field = FilteringProducts(this, originalList)
            }
            return field
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()
            val productImage = product.productImageUris

            productImage?.let {
                for (imageUri in it) {
                    imageList.add(SlideModel(imageUri.toString()))
                }
            }

            ivImageSlider.setImageList(imageList)
            tvProductTitle.text = product.productName
            val productQuantity = "${product.productQuantity} ${product.productUnit}"
            tvProductQuantity.text = productQuantity
            tvProductPrice.text = "₹${product.productPrice}"
        }

        holder.itemView.setOnClickListener {
            onEditClicked(product)
        }
    }

    override fun getFilter(): Filter {
        return filter as FilteringProducts // Use the filter from the custom getter
    }
}

