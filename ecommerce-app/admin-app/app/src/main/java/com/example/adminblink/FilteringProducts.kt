package com.example.adminblink

import android.util.Log
import android.widget.Filter
import com.example.adminblink.adapter.AdapterProduct
import com.example.adminblink.model.Product
import java.util.Locale

class FilteringProducts(
    val adapter: AdapterProduct,
    val filter: ArrayList<Product>
):Filter(){
    override fun performFiltering(constraint: CharSequence?): FilterResults {


        val result = FilterResults()
        if(!constraint.isNullOrEmpty()) {
            val filteredList = ArrayList<Product>()
            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")

            for (product in filter){
                if (query.any {
                                product.productName?.uppercase(Locale.getDefault())?.contains(it) == true ||
                                product.productCategory?.uppercase(Locale.getDefault())?.contains(it) == true ||
                                product.productPrice?.toString()?.uppercase(Locale.getDefault())?.contains(it) == true ||
                                product.productType?.uppercase(Locale.getDefault())?.contains(it) == true
                    }){
                    filteredList.add(product)
                }

            }

            result.values = filteredList
            result.count = filteredList.size

            Log.d("FilteringProducts", "Filtering query: $query, Results: ${filteredList.size}")
        }
        else{
            result.values = filter
            result.count = filter.size
        }

        return result
    }

    override fun publishResults(p0: CharSequence?, result: FilterResults?) {
        adapter.differ.submitList(result?.values as ArrayList<Product>)

    }
}