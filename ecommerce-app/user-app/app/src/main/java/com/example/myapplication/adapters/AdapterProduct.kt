package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.myapplication.FilteringProducts
import com.example.myapplication.databinding.ItemViewProductBinding
import com.example.myapplication.models.Product

class AdapterProduct(
    val onAddButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onIncrementButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onDecrementButtonClicked: (Product, ItemViewProductBinding) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() , Filterable{

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

            if (product.itemCount!! > 0){
                tvCount.text = product.itemCount.toString()
                tvAdd.visibility = View.GONE
                productCount.visibility = View.VISIBLE
            }

            tvAdd.setOnClickListener { onAddButtonClicked(product,this)}
            tvIncrement.setOnClickListener { onIncrementButtonClicked(product,this) }
            tvDecrement.setOnClickListener { onDecrementButtonClicked(product,this) }

            
        }


    }

    override fun getFilter(): Filter {
        return filter as FilteringProducts // Use the filter from the custom getter
    }


}

