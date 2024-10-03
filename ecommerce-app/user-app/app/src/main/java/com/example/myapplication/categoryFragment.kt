package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.adapters.AdapterProduct
import com.example.myapplication.databinding.FragmentCategoryBinding
import com.example.myapplication.databinding.ItemViewProductBinding
import com.example.myapplication.models.Product
import com.example.myapplication.roomdb.CartProductTable
import com.example.myapplication.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class categoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private val viewModel : UserViewModel by viewModels()
    private var category: String? = null
    private lateinit var adapterProduct: AdapterProduct
    private var cartlistener: Cartlistener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        getProductCategory()
        setToolbarTitle()
        onSearchMenuClicked()
        onNavigationClicked()
        fetchCategoryProducts()
        return binding.root
    }

    private fun onNavigationClicked() {
        binding.tbCategoryFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun onSearchMenuClicked() {
        binding.tbCategoryFragment.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.searchMenu -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchCategoryProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch{


            viewModel.getCategoryProducts(category!!).collect{
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE

                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked, ::onDecrementButtonClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
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

    private fun setToolbarTitle() {
        binding.tbCategoryFragment.title = category
    }

    private fun getProductCategory() {
            val bundle = arguments
            category = bundle?.getString("category")
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