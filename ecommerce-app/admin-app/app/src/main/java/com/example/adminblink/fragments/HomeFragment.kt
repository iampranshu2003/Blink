package com.example.adminblink.fragments

import AdminViewModel
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminblink.Constants
import com.example.adminblink.R
import com.example.adminblink.Utils
import com.example.adminblink.adapter.AdapterProduct
import com.example.adminblink.adapter.CategoriesAdapter
import com.example.adminblink.databinding.EditProductLayoutBinding
import com.example.adminblink.databinding.FragmentHomeBinding
import com.example.adminblink.model.Categories
import com.example.adminblink.model.Product
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterProduct: AdapterProduct
    val viewModel : AdminViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBars()
        setCategories()
        searchProducts()
        getAllProducts("All")
        // Inflate the layout for this fragment
        return binding.root 
    }

    private fun searchProducts() {
        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                Log.d("HomeFragment", "Search query: $query")
                adapterProduct.filter?.filter(query)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun getAllProducts(category: String) {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts(category).collect {
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE

                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(::onEditClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE

            }
        }
    }

    private fun setCategories() {
        val categoryList = ArrayList<Categories>()

        for (i in 0 until Constants.allProductCategoryImages.size){
            val category = Categories(Constants.allProductCategory[i], Constants.allProductCategoryImages[i])
            categoryList.add(category)

        }

        binding.rvCategories.adapter = CategoriesAdapter(categoryList, ::onCategoryClicked)

    }

    private fun onCategoryClicked(categories: Categories){
        getAllProducts(categories.category )

    }
    private fun onEditClicked(product: Product){
        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
            etProductName.setText(product.productName)
            etProductPrice.setText(product.productPrice.toString())
            etProductQuantity.setText(product.productQuantity.toString())
            etProductUnit.setText(product.productUnit)
            etProductCategory.setText(product.productCategory)
            etProductStock.setText(product.productStock.toString())
            etProductType.setText(product.productType)


        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()


        editProduct.btnEditProduct.setOnClickListener {
            editProduct.apply {
                editProduct.etProductName.isEnabled = true
                editProduct.etProductPrice.isEnabled = true
                editProduct.etProductQuantity.isEnabled = true
                editProduct.etProductUnit.isEnabled = true
                editProduct.etProductCategory.isEnabled = true
                editProduct.etProductStock.isEnabled = true
                editProduct.etProductType.isEnabled = true
            }
        }
        setAutoCompleteTextView(editProduct)

        editProduct.btnSaveProduct.setOnClickListener {

            lifecycleScope.launch {


                product.productName = editProduct.etProductName.text.toString()
                product.productPrice = editProduct.etProductPrice.text.toString().toInt()
                product.productQuantity = editProduct.etProductQuantity.text.toString()
                product.productUnit = editProduct.etProductUnit.text.toString()
                product.productCategory = editProduct.etProductCategory.text.toString()
                product.productStock = editProduct.etProductStock.text.toString().toInt()
                product.productType = editProduct.etProductType.text.toString()
                viewModel.SavingUpdateProducts(product)
            }



            Utils.showToast(requireContext(), "Product Updated")
            alertDialog.dismiss()





        }



    }

    private fun setAutoCompleteTextView(editProduct: EditProductLayoutBinding) {
        val units =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsofProducts)
        val category =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductCategory)
        val productType =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductType)
        editProduct.apply {
            etProductQuantity.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }
    }

    private fun setStatusBars(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}