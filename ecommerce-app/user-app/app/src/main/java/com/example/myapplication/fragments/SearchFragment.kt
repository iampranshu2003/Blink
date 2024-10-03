package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.Cartlistener
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.adapters.AdapterProduct
import com.example.myapplication.databinding.FragmentSearchBinding
import com.example.myapplication.databinding.ItemViewProductBinding
import com.example.myapplication.models.Product
import com.example.myapplication.roomdb.CartProductTable
import com.example.myapplication.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {


    private lateinit var binding: FragmentSearchBinding
    val viewModel: UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private var cartlistener: Cartlistener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        getAllProducts()
        searchProducts()
        backToHomeFragment()
        return binding.root
    }

    private fun searchProducts() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
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

    private fun backToHomeFragment() {
        binding.backSearch.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

    }

    private fun getAllProducts() {

        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts().collect {
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE

                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(
                    ::onAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE

            }
        }
    }
    private fun onAddButtonClicked(product: Product, itemViewProductBinding: ItemViewProductBinding) {
        itemViewProductBinding.tvAdd.visibility = View.GONE
        itemViewProductBinding.productCount.visibility = View.VISIBLE

        //step1
        var itemCount = itemViewProductBinding.tvCount.text.toString().toInt()
        itemCount++
        itemViewProductBinding.tvCount.text = itemCount.toString()
        cartlistener?.showCartLayout(1)



        //step2
        product.itemCount = itemCount

        lifecycleScope.launch {
            cartlistener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCount)
        }


    }
    private fun onIncrementButtonClicked(product: Product, itemViewProductBinding: ItemViewProductBinding) {
        var itemCountincrement = itemViewProductBinding.tvCount.text.toString().toInt()
        itemCountincrement++

        if (product.productStock!! + 1 > itemCountincrement){
            itemViewProductBinding.tvCount.text = itemCountincrement.toString()
            cartlistener?.showCartLayout(1)


            //step2
            product.itemCount = itemCountincrement
            lifecycleScope.launch {
                cartlistener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(product, itemCountincrement)
            }
        }

        else{
            Utils.showToast(requireContext(), "Out of Sto ck")
        }



    }
    private fun onDecrementButtonClicked(product: Product, itemViewProductBinding: ItemViewProductBinding) {
        var itemCountDecrement = itemViewProductBinding.tvCount.text.toString().toInt()
        itemCountDecrement--

        product.itemCount = itemCountDecrement
        lifecycleScope.launch {
            cartlistener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCountDecrement)

        }
        if (itemCountDecrement>0) {
            itemViewProductBinding.tvCount.text = itemCountDecrement.toString()
        } else {
            lifecycleScope.launch {
                viewModel.deleteCartProduct(product.productRandomID!!)
            }
            itemViewProductBinding.tvAdd.visibility = View.VISIBLE
            itemViewProductBinding.productCount.visibility = View.GONE
            itemViewProductBinding.tvCount.text = "0"
        }
        cartlistener?.showCartLayout(-1)

        //step2


    }

    private fun saveProductInRoomDb(product: Product) {
        val cartProduct = CartProductTable(
            productID = product.productRandomID!!,
            productName = product.productName,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹"+"${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0),
            productCategory = product.productCategory,
            adminUID = product.adminUID

        )
        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Cartlistener){
            cartlistener = context
        } else {
            throw ClassCastException(" must implement Cartlistener")
        }


    }
}