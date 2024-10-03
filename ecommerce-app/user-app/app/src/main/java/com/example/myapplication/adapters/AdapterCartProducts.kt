package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemViewCartProductsBinding
import com.example.myapplication.roomdb.CartProductTable

class AdapterCartProducts: RecyclerView.Adapter<AdapterCartProducts.CartProductsViewHolder>() {
    class CartProductsViewHolder(val binding: ItemViewCartProductsBinding): ViewHolder(binding.root)


    val diffUtil = object : DiffUtil.ItemCallback<CartProductTable>(){
        override fun areItemsTheSame(
            oldItem: CartProductTable,
            newItem: CartProductTable): Boolean {
            return oldItem.productID == newItem.productID
        }

        override fun areContentsTheSame(
            oldItem: CartProductTable,
            newItem: CartProductTable): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(ItemViewCartProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(product.productImage).into(ivProductImage)
            tvProductTitle.text = product.productName
            tvProductPrice.text = product.productPrice.toString()
            tvProductQuantity.text = product.productQuantity.toString()
            tvProductCount.text = product.productCount.toString()
        }
    }


}